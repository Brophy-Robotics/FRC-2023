package frc.robot.commands.alignWithApriltag;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Swerve;

public class AlignWithApriltag_Subcommand_Three extends CommandBase {
    
    // Strafe left or right until we can see the april tag

    private Swerve swerve;

    public AlignWithApriltag_Subcommand_Three(Swerve swerve) {
        this.swerve = swerve;
        addRequirements(swerve);
    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interrupted) {
        swerve.brake();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
