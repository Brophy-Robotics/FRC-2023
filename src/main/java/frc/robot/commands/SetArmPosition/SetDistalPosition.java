package frc.robot.commands.setArmPosition;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DoubleArm;

public class SetDistalPosition extends CommandBase { // small arm

    private DoubleArm doubleArm;
    private double distalPosition;
    private boolean greater;

    public SetDistalPosition(DoubleArm doubleArm, double distalPosition) {
        this.doubleArm = doubleArm;
        addRequirements(doubleArm);

        this.distalPosition = distalPosition;
    }

    @Override
    public void initialize() {
        doubleArm.setTargetAngles(new double[] {doubleArm.getTargetArmAngles()[0], distalPosition});
        greater = doubleArm.getCurrentArmAngles()[1] < distalPosition;
    }

    @Override
    public void execute() {
        doubleArm.pidPowerArm();
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            doubleArm.resetPID();
        } else {
            doubleArm.brake();
        }
    }
    
    @Override
    public boolean isFinished() {
        return (doubleArm.getCurrentArmAngles()[1] > distalPosition) == greater;
    }

}
