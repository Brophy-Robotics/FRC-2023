package frc.robot.commands.Intake;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Claw;
import frc.robot.subsystems.DoubleArm;

import static frc.robot.Constants.TuningConstants.*;

import java.util.function.BooleanSupplier;

import static frc.robot.Constants.IntakeConstants.*;

public class Intake_Subcommand extends CommandBase {
    // Plan: move to intake position, then intake for some 
                // amount of time, then finish
    
    private Claw claw;
    private DoubleArm doubleArm;

    private boolean isFirstAction = true;

    private BooleanSupplier stopSup;

    private double starting_time;

    public Intake_Subcommand(Claw claw, DoubleArm doubleArm, BooleanSupplier stopSup) {
        this.doubleArm = doubleArm;
        this.claw = claw;
        addRequirements(claw);

        this.stopSup = stopSup;
        
        isFirstAction = true;
        starting_time = System.currentTimeMillis() / 1000.0;
    }

    @Override
    public void execute() {
        if (isFirstAction) {
            claw.intake();
            isFirstAction = false;
            doubleArm.resetWhipControl();
            doubleArm.setTargetPositions(intakePosition);
            starting_time = System.currentTimeMillis() / 1000.0;
        }
    }
    
    @Override
    public boolean isFinished() {
        if ((stopSup.getAsBoolean()) || ((claw.getCurrent() > claw_current_limit) && (System.currentTimeMillis() / 1000.0 - starting_time > 0.8))) {
            claw.stop();
            doubleArm.brake();
            doubleArm.setTargetPositions(idlePosition);
            isFirstAction = true;
            SmartDashboard.putBoolean("claw reset", claw.getCurrent() > claw_current_limit);
            SmartDashboard.putBoolean("stop manual", stopSup.getAsBoolean());
            return true;
        }
        return false;
    }
}
