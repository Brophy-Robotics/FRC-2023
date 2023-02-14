package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.autos.*;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

import static frc.robot.Constants.AprilTagAlignmentConstants.*;
import static frc.robot.Constants.SwerveConstants.*;
import static frc.robot.Constants.DoubleArmConstants.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    /* Controllers */
    private final XboxController driver = new XboxController(0);
    private final XboxController operator = new XboxController(1);

    /* Driver Buttons */
    private final JoystickButton zeroGyro = new JoystickButton(driver, zeroGyroButton);
    private final JoystickButton robotCentric = new JoystickButton(driver, robotCentricButton);
    private final JoystickButton tagAligner = new JoystickButton(driver, tagAlignerButton);
    private final JoystickButton brake = new JoystickButton(driver, brakeButton);

    /* Operator Buttons */
    private final JoystickButton topGoal = new JoystickButton(operator, topGoalButton);
    private final JoystickButton resetEncoders = new JoystickButton(operator, resetEncodersButton);
    private final JoystickButton rawPower = new JoystickButton(operator, rawPowerButton);

    /* Subsystems */
    private final Swerve s_Swerve = new Swerve();
    private final DoubleArm doubleArm = new DoubleArm();

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        s_Swerve.setDefaultCommand(
            new TeleopSwerve(
                s_Swerve, 
                () -> -driver.getRawAxis(translationAxis), 
                () -> -driver.getRawAxis(strafeAxis), 
                () -> -driver.getRawAxis(rotationAxis), 
                () -> robotCentric.getAsBoolean(), 
                () -> -driver.getPOV(), 
                () -> 1 - 0.75 * driver.getRawAxis(slowAxis) // what we multiply translation speed by; rotation speed is NOT affected
            )
        );

        doubleArm.setDefaultCommand(
            new TeleopDoubleArm(
                doubleArm, 
                () -> operator.getRawAxis(horizAxis),
                () -> -operator.getRawAxis(vertAxis)
            )
        );

        // Configure the button bindings
        configureButtonBindings();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroGyro()));
        tagAligner.toggleOnFalse(new AlignWithApriltag(s_Swerve, driver));
                    // toggle on false because otherwise it throws an error :(
        
        brake.toggleOnFalse(new Brake(s_Swerve));
        
        /* Operator Buttons */
        resetEncoders.toggleOnFalse(new InstantCommand(() -> doubleArm.resetEncoders()));
        topGoal.toggleOnTrue(new InstantCommand(() -> doubleArm.setTargetPositions(40, 14)));
        rawPower.onTrue(new TestingDoubleArm(
            doubleArm, 
            () -> operator.getRawAxis(motorOneAxis), 
            () -> -operator.getRawAxis(motorTwoAxis)
        ));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return new exampleAuto(s_Swerve);
    }
}
