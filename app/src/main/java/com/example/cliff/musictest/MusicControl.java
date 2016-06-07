package com.example.cliff.musictest;

/**
 * Created by cliff on 2016/6/5.
 */
public class MusicControl {

    public static final int play = 0;
    public static final int pause = 1;
    public static final int stop = 2;

    private int control = 3;
    public MusicControl(int control){
        if(control == 0||control == 1||control == 2){
            this.control = control;
        }
    }

    public String  getControl() {
        switch (control){
            case MusicControl.play:
                return "Pause";
            case MusicControl.pause:
                return "Play";
            case MusicControl.stop:
                return "Play";
            default:
                return null;
        }
    }

    public void setControl(int control) {
        this.control = control;
    }
}
