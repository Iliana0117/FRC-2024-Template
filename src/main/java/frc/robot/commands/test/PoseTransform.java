package frc.robot.commands.test;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.constants.miscConstants.TestConstants;
import frc.robot.subsystems.DrivetrainImpl;

/**
 * Tests the odometry of the robot by driving a certain distance and calculating the error.
 */
public class PoseTransform extends CommandBase {

    private final DrivetrainImpl drive;

    private double startTime;
    private Pose2d finalPose;
    private final Transform2d distanceToMove;
    private Pose2d error;

    public PoseTransform(DrivetrainImpl drive, Transform2d poseTransform) {
        this.drive = drive;
        // finalPose is position after robot moves from current position-- startPose-- by the values that are inputted-- distanceToMove
        distanceToMove = poseTransform;

        addRequirements(drive);
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        finalPose = drive.getPose().transformBy(distanceToMove);
    }

    @Override
    public void execute() {
        drive.runChassisPID(finalPose.getX(), finalPose.getY(), finalPose.getRotation().getRadians());
    }

    @Override
    public boolean isFinished() {
        // TODO: the current PID values don't allow the command to finish
        double errorMarginMeters = TestConstants.TRANSLATION_ERROR;
        double errorMarginRadians = Units.degreesToRadians(10);
        error = drive.getPose().relativeTo(finalPose);
        // if robot thinks its precision is < 0.1 to the target we inputted, it will stop, so then we can see how off it is
        return Math.abs(error.getX()) < errorMarginMeters && Math.abs(error.getY()) < errorMarginMeters && Math.abs(error.getRotation().getRadians()) < errorMarginRadians;
    }

    @Override
    public void end(boolean interrupted) {
        drive.stop();
        System.out.println(Timer.getFPGATimestamp() - startTime);
        System.out.println(error.getX());
        System.out.println(error.getY());
        System.out.println(error.getRotation().getRadians());
    }
}