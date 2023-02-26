package frc.robot.commands.AlignWithApriltag;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.SetArmPosition.SetArmPosition;
import frc.robot.subsystems.DoubleArm;
import frc.robot.subsystems.Swerve;

import static frc.robot.Constants.TuningConstants.*;

public class AlignWithApriltag extends SequentialCommandGroup {
    public AlignWithApriltag(Swerve swerve, DoubleArm doubleArm) {
        addRequirements(swerve, doubleArm); // means that other functions are not allowed to access it
        addCommands(
            new SetArmPosition(doubleArm, idlePosition), 
            new AlignWithApriltag_Subcommand_One(swerve), 
            new AlignWithApriltag_Subcommand_Two(swerve), 
            new AlignWithApriltag_Subcommand_Three(swerve), 
            new AlignWithApriltag_Subcommand_Four(swerve), 
            new AlignWithApriltag_Subcommand_Five(swerve), 
            new AlignWithApriltag_Subcommand_Six(doubleArm)
        );
    }
}
