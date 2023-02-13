package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogEncoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.DoubleArmConstants.*;

public class DoubleArm extends SubsystemBase {

    private CANSparkMax first_motor, first_motor_follower, second_motor;
    private AnalogEncoder first_encoder, second_encoder;

    public double[] target_xy = new double[2];
    private double[] target_positions = new double[2];

    public DoubleArm() { // Initialize the motors, encoders, and target positions
        first_motor = new CANSparkMax(first_motor_ID, MotorType.kBrushless); // NEO Motors are brushless
        first_motor_follower = new CANSparkMax(first_motor_follower_ID, MotorType.kBrushless);
        second_motor = new CANSparkMax(second_motor_ID, MotorType.kBrushless);

        first_motor.setInverted(invert_first_motor);
        first_motor_follower.follow(first_motor, false); // follows it in the same direction
                    // we may have to change this part, I'm not sure
        second_motor.setInverted(invert_second_motor);
        // we want it so powering the motors rotates the arms counterclockwise

        first_encoder = new AnalogEncoder(first_encoder_channel);
        second_encoder = new AnalogEncoder(second_encoder_channel);
        // we want it so the encoder increases when the arm goes counterclockwise - may have to adjust them

        first_encoder.setDistancePerRotation(360.0 * (invert_first_encoder ? -1 : 1)); // no gear ratio
        second_encoder.setDistancePerRotation(360.0 * (invert_second_encoder ? -1 : 1)); // put this negative if it increases the wrong way

        target_positions[0] = first_encoder.getDistance();
        target_positions[1] = first_encoder.getDistance(); 
                            // if we want to reset encoders, run resetEncoders()
                            // reset encoders before Autonomous and Testing, not before TeleOp tho
        
        target_xy[0] = first_arm_length * Math.cos(target_positions[0] * Math.PI / 180.0) + 
                       second_arm_length * Math.cos(target_positions[1] * Math.PI / 180.0);

        target_xy[1] = first_arm_length * Math.sin(target_positions[0] * Math.PI / 180.0) + 
                       second_arm_length * Math.sin(target_positions[1] * Math.PI / 180.0);

        Timer.delay(1.0);
    }

    public void powerArm(double horizontalTarget, double verticalTarget) { // in RobotContainer have a function to make them continuous
        setTargetPositions(horizontalTarget, verticalTarget);

        double[] current_angles = getAbsoluteArmAngles();

        first_motor.set(pidPower(
            target_positions[0] - current_angles[0], 
            first_motor_max_power, 
            first_motor_min_error, 
            first_motor_max_error
        ));

        second_motor.set(pidPower(
            target_positions[1] - current_angles[1], 
            second_motor_max_power, 
            second_motor_min_error, 
            second_motor_max_error
        ));
        // set power to arm modules
    }

    public void resetEncoders() {
        first_encoder.reset();
        second_encoder.reset();
    }

    public void setTargetPositions(double x, double y) {

        if (x <= min_x) x = min_x; // don't let x be too close
        if (y <= min_y) y = min_y; // don't let y be too low

        double radius = Math.sqrt(x * x + y * y); // we don't have to worry about divide by zero errors because x > 0.25
        if (radius < clipping_one * (first_arm_length - second_arm_length)) {
            x *= clipping_one * (first_arm_length - second_arm_length) / radius;
            y *= clipping_one * (first_arm_length - second_arm_length) / radius;
            radius = clipping_one * (first_arm_length - second_arm_length);
        } else if (radius > clipping_two * (first_arm_length + second_arm_length)) {
            x *= clipping_two * (first_arm_length + second_arm_length) / radius;
            y *= clipping_two * (first_arm_length + second_arm_length) / radius;
            radius = clipping_two * (first_arm_length + second_arm_length);
        } // clip radius to range

        target_xy[0] = x;
        target_xy[1] = y;

        double angle = Math.atan(y / x) * 180.0 / Math.PI; // because x > 0 we don't have to worry about adding pi
        
        // set target 1 and target 2

        double first_angle = Math.acos((radius * radius + first_arm_length * first_arm_length - second_arm_length * second_arm_length) / (2.0 * first_arm_length * radius)) * 180.0 / Math.PI;
        double second_angle = Math.acos((radius * radius + second_arm_length * second_arm_length - first_arm_length * first_arm_length) / (2.0 * second_arm_length * radius)) * 180.0 / Math.PI;
                // not the target angles, just something that's useful
                // angles of triangle between both arms and the radial vector
                // Law of Cosines: a^2 + b^2 - 2ab cos(C) = c^2 --> cos(C) = (a^2 + b^2 - c^2) / (2ab)

                // angle between first arm and radial vector and between second arm and radial vector
                // this means that our target angles are the radial vector plus or minus these angles
                // note, these are the target absolute angles, not the target relative angles

        target_positions[0] = angle + (angle < switching_angle ? 0 - first_angle : first_angle);
        target_positions[1] = angle + (angle < switching_angle ? second_angle : 0 - second_angle);
            // if we are greater than our switching angle, then we are concave down; if we are less, then we are concave down
    }

    public double[] getAbsoluteArmAngles() {
        return new double[] {
            first_encoder.getDistance(), 
            first_encoder.getDistance() + second_encoder.getDistance()
        };
    }

    public double[] getData() {
        return new double[] {
            first_encoder.getDistance(), 
            second_encoder.getDistance(),
            first_encoder.getDistance() + second_encoder.getDistance(),
            pidPower(
                target_positions[0] - (first_encoder.getDistance()), 
                first_motor_max_power, 
                first_motor_min_error, 
                first_motor_max_error),
            pidPower(
                target_positions[1] - (first_encoder.getDistance() + second_encoder.getDistance()), 
                second_motor_max_power, 
                second_motor_min_error, 
                second_motor_max_error), 
            target_xy[0], 
            target_xy[1], 
            target_positions[0], 
            target_positions[1]
        };
    }

    public double pidPower(double error, double maxPower, double minDegreesOff, double maxDegreesOff) {
        int multiplier = 1;
        if (error < 0) {
            error = 0 - error;
            multiplier = -1;
        }

        if (error < minDegreesOff) return 0;

        error /= maxDegreesOff;
        if (error > 1) error = 1;

        return maxPower * multiplier * Math.pow(error, k_exponent);

    }

    @Override
    public void periodic() { // PID and Telemetry
        double[] data = getData();
        SmartDashboard.putNumber("Arm 1 Angle", data[0]);
        SmartDashboard.putNumber("Arm 2 Relative Angle", data[1]);
        SmartDashboard.putNumber("Arm 2 Absolute Angle", data[2]);
        SmartDashboard.putNumber("Arm 1 target power", data[3]);
        SmartDashboard.putNumber("Arm 2 target power", data[4]);
        SmartDashboard.putNumber("Target x", data[5]);
        SmartDashboard.putNumber("Target y", data[6]);
        SmartDashboard.putNumber("Target 1st Angle", data[7]);
        SmartDashboard.putNumber("Target 2nd Angle", data[8]);
    }
}