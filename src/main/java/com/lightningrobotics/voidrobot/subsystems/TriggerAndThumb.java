package com.lightningrobotics.voidrobot.subsystems;

import edu.wpi.first.wpilibj2.command.button.Button;

public class TriggerAndThumb extends Button {
    Button a;
    Button b;

    public TriggerAndThumb(Button a, Button b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean get() {
        return a.get() && b.get();
    }
    
}
