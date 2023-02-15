package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.ClawConstants.*;

public class Claw extends SubsystemBase {
    
    private CANSparkMax claw_motor;

    public Claw() {
        claw_motor = new CANSparkMax(claw_motor_ID, MotorType.kBrushless);
        claw_motor.restoreFactoryDefaults();
        claw_motor.setInverted(false);

        if (claw_motor_brake) {
            claw_motor.setIdleMode(IdleMode.kBrake);
        } else {
            claw_motor.setIdleMode(IdleMode.kCoast);
        }

        Timer.delay(1.0);
    }

    public void intake() {
        claw_motor.set(intake_power);
    }

    public void outtake() {
        claw_motor.set(outtake_power);
    }

    public void stop() {
        claw_motor.set(0);
    }
}