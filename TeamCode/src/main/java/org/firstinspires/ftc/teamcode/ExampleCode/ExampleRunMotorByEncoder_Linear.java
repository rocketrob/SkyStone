/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode.ExampleCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file illustrates the concept of  running a motor based on encoder counts.
 * Hardware configuration will need to match your robots
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Example RunMotor By Encoder", group="Examples")
@Disabled
public class ExampleRunMotorByEncoder_Linear extends LinearOpMode {

    //reset runtime counter
    private ElapsedTime runtime = new ElapsedTime();

    //  DON'T FORGET TO RENAME HARDWARE CONFIG FILE NAME HERE!!!!!!
    MyBotHardwareSetup robot = new MyBotHardwareSetup(); //set up remote to robot hardware configuration


    //variables for determining distance travels per encoder 'clicks'
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder is 1440, AndyMark NevaRest encoders is 1120
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP >1.0 if geared DOWN. If no gearing set to 1.0
    // not used here: static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    // Not used here: static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    //Additional variables
    int     armHoldPosition;             // reading of arm position when buttons released to hold
    double  slopeVal         = 2000.0;   // increase or decrease to perfect holding power



    @Override
    public void runOpMode() {
         /*
         * Use the hardwareMap to get the dc motors and servos by name. Note
         * that the names of the devices must match the names used when you
         * configured your robot and configuration file.
         */
        robot.init(hardwareMap);  //Initialize hardware from the Hardware Setup Class

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        //Reset Encoders
        robot.motorArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();   //allow for reset

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

       /* ///////////////
       // Establish encoder target and run motor to them
       */ //////////////
        int positionUp = (int)COUNTS_PER_MOTOR_REV/2;//divide full rotation 'click' count by desired (half a rotation in this case). And cast from double to int
        int positionDown = robot.motorArm.getCurrentPosition() - positionUp; // return to initial position

        //move are up
        if(gamepad2.a) {
            int target = robot.motorArm.getCurrentPosition() + positionUp;
            robot.motorArm.setTargetPosition(target); // set desired target (determined above)
            robot.motorArm.setPower(1.0); // set motor power
            robot.motorArm.setMode(DcMotor.RunMode.RUN_TO_POSITION); // run motor to that position

            // wait for arm to reach target before moving on to next line of code. display arm position while waiting
            while (robot.motorArm.isBusy()) {
                // Send telemetry message to indicate successful Encoder reset to zero
                telemetry.addData("moving to target", "at %7d :%7d", robot.motorArm.getCurrentPosition());
                telemetry.update();
            }
        }

        // move arm down
        else if(gamepad2.b) {
            int target = robot.motorArm.getCurrentPosition() + positionDown; //add a neg should be negative
            robot.motorArm.setTargetPosition(target); // set desired target (determined above)
            robot.motorArm.setPower(-0.5); // set motor power
            robot.motorArm.setMode(DcMotor.RunMode.RUN_TO_POSITION); // run motor to that position

            // wait for motor to reach target before moving on to next line of code. display arm position while waiting
            while (robot.motorArm.isBusy()) {
                // Send telemetry message to indicate successful Encoder reset to zero
                telemetry.addData("moving to target", "at %7d :%7d", robot.motorArm.getCurrentPosition());
                telemetry.update();
            }
        }else {
            // Stop and reset motor
            //robot.motorArm.setPower(0.0);
            // or
            // set to hold that position
            robot.motorArm.setPower((double) (armHoldPosition - robot.motorArm.getCurrentPosition()) / slopeVal);
        }
    }
}
