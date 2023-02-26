package frc.robot.commands.AlignWithApriltag;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DoubleArm;

public class AlignWithApriltag_Subcommand_Six extends CommandBase {
    
    // put the arm to where we want it to be

    private DoubleArm doubleArm;

    public AlignWithApriltag_Subcommand_Six(DoubleArm doubleArm) {
        this.doubleArm = doubleArm;
        addRequirements(doubleArm);
    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interrupted) {
        doubleArm.brake();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

}
