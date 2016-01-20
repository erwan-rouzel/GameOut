package fr.ecp.sio.gameout.salon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ecp.sio.gameout.GameActivity;
import fr.ecp.sio.gameout.MainActivity;
import fr.ecp.sio.gameout.R;
import fr.ecp.sio.gameout.salon.message.Message;
import fr.ecp.sio.gameout.salon.message.ParticipantMessage;
import fr.ecp.sio.gameout.salon.message.TurnMessage;

public class OnlineGameActivity extends ActionBarActivity implements
        OnInvitationReceivedListener,
        RoomUpdateListener,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener {

    private final static String TAG = OnlineGameActivity.class.getSimpleName();

    // Google Api Client
    private static GoogleApiClient mGoogleApiClient;

    // AlertDialog for showing messages to the user
    private AlertDialog mAlertDialog;

    // request code for the "select players" UI
    // can be any number as long as it's unique
    // Intent codes used in startActivityForResult
    private final static int RC_SELECT_PLAYERS = 10000;
    private final static int RC_WAITING_ROOM = 10003;
    private static final int RC_SIGN_IN = 9001;

    // RealTime Multiplayer Room, null when not connected
    private Room mRoom;

    // The player's participant id.  This maps to GameOutParticipant.persistentId
    private String mMyPersistentId;

    // Jackson JSON Processing
    private ObjectMapper mMapper;

    // Map of all participants in the match, with key being the participant ID for fast lookup
    private HashMap<String, GameOutParticipant> mParticipants = new HashMap<>();

    // Participants that were in this match at one time, but left
    private HashMap<String, GameOutParticipant> mOldParticipants = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate ");
        setContentView(R.layout.activity_online_game);

        // Get GoogleApiClient instance
        mGoogleApiClient = GoogleApiClientSingleton
                .getInstance()
                .getApiClient();

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.

        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);


        // Get invitation from Bundle
        if (savedInstanceState != null) {
            Invitation invitation = savedInstanceState.getParcelable(Multiplayer.EXTRA_INVITATION);
            if (invitation != null) {
                onInvitationReceived(invitation);
            }
        }

        findViewById(R.id.quick_match_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickGame();
            }
        });

        findViewById(R.id.invite_players_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select between 1 and 3 players (not including the current one, so the game has 2-4 total)
                int minPlayers = 1;
                int maxPlayers = 3;
                Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient,
                        minPlayers, maxPlayers, true);
                startActivityForResult(intent, RC_SELECT_PLAYERS);


            }
        });

        findViewById(R.id.show_invitations_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnlineGameActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void startQuickGame() {
        Log.d(TAG,"startQuickGame: ");
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
        startActivity(new Intent(this,GameActivity.class));
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        Log.i(TAG, "onActivityResult: code = " + request + ", response = " + response);

        // Coming back from a RealTime Multiplayer waiting room
        if (request == RC_WAITING_ROOM) {
            dismissSpinner();

            Room room = data.getParcelableExtra(Multiplayer.EXTRA_ROOM);
            if (response == RESULT_OK) {
                Log.d(TAG, "Waiting Room: Success");
                mRoom = room;
                startMatch();
            } else if (response == RESULT_CANCELED) {
                Log.d(TAG, "Waiting Room: Canceled");
                leaveRoom();
            } else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                Log.d(TAG, "Waiting Room: Left Room");
                leaveRoom();
            } else if (response == GamesActivityResultCodes.RESULT_INVALID_ROOM) {
                Log.d(TAG, "Waiting Room: Invalid Room");
                leaveRoom();
            }
        }

        // We are coming back from the player selection UI, in preparation to start a match.
        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                Log.d(TAG, "onActivityResult: user canceled player selection.");
                return;
            }

            // Create a basic room configuration
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();

            // Set the auto match criteria
            int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
                Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }

            // Set the invitees
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
            if (invitees != null && invitees.size() > 0) {
                roomConfigBuilder.addPlayersToInvite(invitees);
            }

            // Build the room and start the match
            showSpinner();
            Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfigBuilder.build());
        }

        if (request == RC_SIGN_IN){
            if (response != Activity.RESULT_OK) {
                // user canceled
                Log.d(TAG, "onActivityResult: sign in failed.");
            }

        }
    }


    private void startMatch(){

    }

    private void endMatch(){

    }

    @Override
    public void onInvitationReceived(final Invitation invitation) {
        Log.d(TAG, "onInvitationReceived:" + invitation);
        final String inviterName = invitation.getInviter().getDisplayName();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getApplicationContext())
                .setTitle("Invitation")
                .setMessage("Would you like to play a new game with " + inviterName + "?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Games.RealTimeMultiplayer.declineInvitation(mGoogleApiClient,
                                invitation.getInvitationId());
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptInvitation(invitation);
                    }
                });

        mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
    }

    /**
     * Accept an invitation to join an RTMP game
     */
    private void acceptInvitation(Invitation invitation) {
        Log.d(TAG, "Got invitation: " + invitation);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this)
                .setInvitationIdToAccept(invitation.getInvitationId());
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Log.d(TAG, "onInvitationRemoved:" + invitationId);

        // The invitation is no longer valid, so dismiss the dialog asking if they'd like to
        // accept and show a Toast.
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }

        Toast.makeText(this.getApplicationContext(), "The invitation was removed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        // TODO: 07/01/16 Voir avec Erwan les informations a recupérer sur le room
        Log.d(TAG, "onRoomCreated: " + statusCode + ":" + room);
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.w(TAG, "Error in onRoomCreated: " + statusCode);
            Toast.makeText(this.getApplicationContext(), "Error creating room.", Toast.LENGTH_SHORT).show();
            dismissSpinner();
            return;
        }

        showWaitingRoom(room);
    }

    /**
     * Show the UI for an RTMP waiting room.
     */
    private void showWaitingRoom(Room room) {
        // Require all players to join before starting
        final int MIN_PLAYERS = Integer.MAX_VALUE;

        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    private void showSpinner() {
        findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
    }

    private void dismissSpinner() {
        findViewById(R.id.progress_layout).setVisibility(View.GONE);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom: " + statusCode + ":" + room);
    }

    @Override
    public void onLeftRoom(int statusCode, String s) {
        Log.d(TAG, "onLeftRoom: " + statusCode + ":" + s);
        mRoom = null;
        // TODO: 06/12/2015 Exit the game
        // updateViewVisibility();
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected: " + statusCode + ":" + room);
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.w(TAG, "Error in onRoomConnected: " + statusCode);
            return;
        }

        mRoom = room;
        // TODO: 06/12/2015 Start the game
        // updateViewVisibility();
    }


    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.d(TAG, "onRealTimeMessageReceived");
        byte[] data = realTimeMessage.getMessageData();

        onMessageReceived(data);
    }

    private void onMessageReceived(byte[] data) {
        Log.d(TAG, "Message: " + new String(data));

        try {
            // Parse JSON message using Jackson
            Message message = mMapper.readValue(data, Message.class);

            if (message.getType().equals(TurnMessage.TAG)) {
                // TODO : Check with Erwan
                // TurnMessage - set all turn-specific data
                TurnMessage msg = mMapper.readValue(data, TurnMessage.class);

            } else if (message.getType().equals(ParticipantMessage.TAG)) {
                // ParticipantMessage - add or remove a participant
                ParticipantMessage msg = mMapper.readValue(data, ParticipantMessage.class);
                GameOutParticipant participant = msg.getGameOutParticipant();

                if (msg.getIsJoining()) {
                    if (mOldParticipants.containsKey(participant.getPersistentId())) {
                        Log.d(TAG, "Participant rejoining: " + participant.getPersistentId());
                        // This participant was in the game before, add and recover
                        GameOutParticipant oldParticipant = mOldParticipants.remove(
                                participant.getPersistentId());
                        mParticipants.put(participant.getPersistentId(), oldParticipant);

                        // Tell everyone what their old score was
                        ParticipantMessage updateMsg = new ParticipantMessage(oldParticipant);
                    } else if (mParticipants.containsKey(participant.getPersistentId())) {
                        // Current participant, update the score
                        mParticipants.get(participant.getPersistentId()).setScore(
                                participant.getScore());
                        onParticipantConnected(participant);
                    } else {
                        // Add new participant
                        onParticipantConnected(participant);
                    }
                } else {
                    onParticipantDisconnected(participant.getMessagingId(), participant.getPersistentId());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not read message.", e);
        }
    }

    /**
     * RTMP Participant joined, register the GameOutParticipant if the Participant is connected.
     *
     * @param p the Participant from the Real-Time Multiplayer match.
     */
    private void onParticipantConnected(Participant p) {
        if (p.isConnectedToRoom()) {
            onParticipantConnected(new GameOutParticipant(p));
        }
    }

    /**
     * Add a GameOutParticipant to the ongoing game and update turn order. If the
     * GameOutParticipant is a duplicate, this method does nothing.
     *
     * @param dp the GameOutParticipant to add.
     */
    private void onParticipantConnected(GameOutParticipant dp) {
        Log.d(TAG, "onParticipantConnected: " + dp.getPersistentId());
        if (!mParticipants.containsKey(dp.getPersistentId())) {
            mParticipants.put(dp.getPersistentId(), dp);
        }

        // TODO: 06/12/2015 Update UI
    }

    /**
     * Remove a participant. If this is RTMP and you are now the only player in the room, leave the
     * room as well and end the game. If this is a Nearby Connections game and the host has
     * disconnected, leave the game and display an error.
     *
     * @param messagingId  the messaging ID of the player that disconnected.
     * @param persistentId the persistent ID of the player that disconnected.
     */
    private void onParticipantDisconnected(String messagingId, String persistentId) {
        Log.d(TAG, "onParticipantDisconnected:" + messagingId);
        GameOutParticipant dp = mParticipants.remove(persistentId);
        if (dp != null) {
            // Display disconnection toast
            Toast.makeText(this.getApplicationContext(), dp.getDisplayName() + " disconnected.", Toast.LENGTH_SHORT).show();

            // Add the participant to the "old" list in case they reconnect
            mOldParticipants.put(dp.getPersistentId(), dp);

            if (mRoom != null && mParticipants.size() <= 1) {
                // Last player left in an RTMP game, leave
                leaveRoom();
            }
        }
    }

    /**
     * Disconnect from an RTMP room or a Nearby Connection. Clear all game data.
     */
    private void leaveGame() {
        if (mRoom != null) {
            leaveRoom();
        }

        mParticipants.clear();
        mOldParticipants.clear();
        mMyPersistentId = null;
    }


    /**
     * Leave an RTMP room.
     */
    private void leaveRoom() {
        Log.d(TAG, "leaveRoom:" + mRoom);
        if (mRoom != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoom.getRoomId());
            mRoom = null;
        }

        //updateViewVisibility();
    }

    @Override
    public void onRoomConnecting(Room room) {
        Log.d(TAG, "onRoomConnecting: " + room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Log.d(TAG, "onRoomAutoMatching: " + room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> strings) {
        Log.d(TAG, "onPeerInvitedToRoom: " + room + ":" + strings);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {

    }

    @Override
    public void onPeerJoined(Room room, List<String> strings) {
        Log.d(TAG, "onPeerJoined: " + room + ":" + strings);
        mRoom = room;
        for (String pId : strings) {
            onParticipantConnected(mRoom.getParticipant(pId));
        }
    }

    @Override
    public void onPeerLeft(Room room, List<String> strings) {
        Log.d(TAG, "onPeerLeft: " + room + ":" + strings);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedRoRoom: " + room);
        mRoom = room;

        // Add self to participants
        mMyPersistentId = mRoom.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        Participant me = mRoom.getParticipant(mMyPersistentId);
        onParticipantConnected(me);
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Log.d(TAG, "onDisconnectedFromRoom: " + room);
        leaveRoom();
    }

    @Override
    public void onPeersConnected(Room room, List<String> strings) {
        Log.d(TAG, "onPeersConnected:" + room + ":" + strings);
        mRoom = room;
        for (String pId : strings) {
            onParticipantConnected(mRoom.getParticipant(pId));
        }
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> strings) {
        Log.d(TAG, "onPeersDisconnected: " + room + ":" + strings);
        for (String pId : strings) {
            onParticipantDisconnected(pId, pId);
        }
    }

    @Override
    public void onP2PConnected(String s) {
        Log.d(TAG, "onP2PConnected: " + s);
    }

    @Override
    public void onP2PDisconnected(String s) {
        Log.d(TAG, "onP2PDisconnected: " + s);
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }


}
