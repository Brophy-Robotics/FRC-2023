package frc.robot.autonomous;

import frc.robot.Constants;
import frc.robot.subsystems.Swerve;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;

import java.io.IOException;
import java.nio.file.Path;

public class RunTrajectory extends SequentialCommandGroup {
    
    public RunTrajectory(Swerve s_Swerve, String fileName) {

        String trajectoryJSON = "pathplanner/generatedJSON/" + fileName + ".wpilib.json";

        Trajectory temp = new Trajectory();

        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(trajectoryJSON);
            temp = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (IOException ex) {
            System.out.println("Unable to open trajectory: " + trajectoryJSON + " " + ex.getStackTrace());
        }
         
        final Trajectory trajectory = temp;

        var thetaController = new ProfiledPIDController(
            Constants.AutoConstants.kPThetaController, 
            0, 
            0, 
        Constants.AutoConstants.kThetaControllerConstraints
        );

        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
            trajectory,
            s_Swerve::getPose,
            Constants.BaseFalconSwerve.swerveKinematics,
            new PIDController(Constants.AutoConstants.kPXController, 0, 0),
            new PIDController(Constants.AutoConstants.kPYController, 0, 0),
            thetaController,
            s_Swerve::setModuleStates,
            s_Swerve
        );

        addCommands(
            new InstantCommand(() -> s_Swerve.resetOdometry(trajectory.getInitialPose())),
            swerveControllerCommand
        );
    }
}
