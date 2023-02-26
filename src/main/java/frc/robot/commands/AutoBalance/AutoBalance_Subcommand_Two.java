package frc.robot.commands.AutoBalance;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Swerve;

import static frc.robot.Constants.AutoBalanceConstants.*;

public class AutoBalance_Subcommand_Two extends CommandBase {
    
    private Swerve swerve;

    public AutoBalance_Subcommand_Two(Swerve swerve) {
        this.swerve = swerve;
        addRequirements(swerve); // means that other functions are not allowed to access it
    }

    @Override
    public void initialize() {
        // we don't need to do anything
    }

    @Override
    public void execute() {
        swerve.drive(new Translation2d(-drive_speed, 0).times(Constants.BaseFalconSwerve.maxSpeed), 0, false, true);
    }

    @Override
    public void end(boolean interrupted) {
        swerve.brake();
    }
    
    @Override
    public boolean isFinished() {
        return Math.abs(swerve.getPitch()) > (pitch_tolerance * 1.2);
    }
}
