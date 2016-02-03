/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.ecp.sio.gameout.salon.message;

/**
 * Created by cquenum on 01/12/2015.
 */

import fr.ecp.sio.gameout.salon.GameOutParticipant;

/**
 * Message indicating that a GameOutParticipant has either joined or left the match.
 *      GameOutParticipant gameOutParticipant - the participant joining or leaving.
 *      boolean isJoining - true if the participant is joining, false if leaving.
 */
public class ParticipantMessage extends Message {

    public static final String TAG = ParticipantMessage.class.getSimpleName();

    private GameOutParticipant gameOutParticipant;
    private boolean isJoining = true;

    /** Default constructor required for Jackson **/
    public ParticipantMessage() {}

    public ParticipantMessage(GameOutParticipant gameOutParticipant) {
        super(TAG);
        this.gameOutParticipant = gameOutParticipant;
    }

    public GameOutParticipant getGameOutParticipant() {
        return gameOutParticipant;
    }

    public void setGameOutParticipant(GameOutParticipant gameOutParticipant) {
        this.gameOutParticipant = gameOutParticipant;
    }

    public boolean getIsJoining() {
        return isJoining;
    }

    public void setIsJoining(boolean isJoining) {
        this.isJoining = isJoining;
    }
}