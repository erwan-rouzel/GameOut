package fr.ecp.sio.gameout.PlayField;

import fr.ecp.sio.gameout.PlayField.PlayFieldPos;

/**
 * Created by od on 11/7/2015.
 * Les positions du jeu dans une static accessible à tous,
 * ceci permet une communication basique entre les threads
 */
public class CurPfp
{
    public static PlayFieldPos pfp = new PlayFieldPos();
}
