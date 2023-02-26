package frc.robot.commands.AutoBalance;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.SetArmPosition.SetArmPosition;
import frc.robot.subsystems.DoubleArm;
import frc.robot.subsystems.Swerve;

import static frc.robot.Constants.TuningConstants.*;

public class AutoBalance extends SequentialCommandGroup {
    public AutoBalance(Swerve swerve, DoubleArm doubleArm) {
        addRequirements(swerve, doubleArm); // means that other functions are not allowed to access it
        addCommands(
            new SetArmPosition(doubleArm, idlePosition), 
            new AutoBalance_Subcommand_One(swerve), // sets target angle of swerve
            new AutoBalance_Subcommand_Two(swerve), // drives until we are on the balance thing
            new AutoBalance_Subcommand_Three(swerve) // autobalances forever
        );
    }
}
