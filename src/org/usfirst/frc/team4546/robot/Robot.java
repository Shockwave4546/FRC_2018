/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// GFB - 1/11/18: Trying to get the PDP voltage to appear on SmartDash
// GFB - 1/13/18: Trying to move one motor with a joystick or a button
// SC, HD, DP - 1/14/18: Trying to get two motors moving and using an xbox controller
// GFB - 1/15/18: Cleaning up 1/14/18's code and trying to get camera code working
// GFB - 1/16/18: Getting a github repository to work with the code

package org.usfirst.frc.team4546.robot;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	//private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	private static final int kPDP = 0;
	private PowerDistributionPanel m_PDP;
	
	private static final int kJoystickPort = 2;
	private XboxController m_xboxcontroller;
	
	private static final int kMotorPort = 0;
	private SpeedController m_motorFRight;
	private static final int kMotorPort2 = 5;
	private SpeedController m_motorFLeft;
	//Two motor drive
	
	private static final int kMotorPort3 = 2;
	private SpeedController m_motorBRight;
	private static final int kMotorPort4 = 4;
	private SpeedController m_motorBLeft;
	//extra motors for 4 wheel drive
	
	private SpeedController m_intake;
	private static final int kIntakePort = 3;
	private SpeedController m_intake2;
	private static final int kIntakePort2 = 1;
	//intake motors
	
	//private boolean toggleY = false;
	//private boolean togglegas = false;
	//private double Rtrigger = 0;
	private boolean driveChange = false;
	private AnalogInput ai;
	double speed1 = .429;
	double speed11 = .4;
	double speed2 = .5;
	double speed22 = .4;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		m_PDP = new PowerDistributionPanel(kPDP);
		SmartDashboard.putData("Voltage", m_PDP);
		m_xboxcontroller = new XboxController(kJoystickPort);
		
		m_motorFLeft = new Talon(kMotorPort);
		m_motorFRight = new Talon(kMotorPort2);
		m_motorBRight = new Talon(kMotorPort3);
		m_motorBLeft = new Talon(kMotorPort4);
		m_motorFRight.setInverted(true);
		m_motorBLeft.setInverted(true);
		//motors
		
		m_intake = new Talon(kIntakePort);
		m_intake2 = new Talon(kIntakePort2);
		m_intake2.setInverted(true);
		//intake motors
		ai = new AnalogInput(0);
		
		// Creates camera and video feed
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(320, 240);
			
			CvSink cvsink = CameraServer.getInstance().getVideo();
			CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 320, 240);
			
			Mat source = new Mat();
			Mat output = new Mat();
			
			while(!Thread.interrupted()) {
				cvsink.grabFrame(source);
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
				outputStream.putFrame(output);
			}
			
		}).start();
		
		
		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {

		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		if (driveChange == false){
			m_motorFLeft.set(speed1);
			m_motorFRight.set(speed2);
			Timer.delay(3);
			m_motorFLeft.set(speed1);
			m_motorFRight.set(speed22);
			Timer.delay(3);
			m_motorFLeft.set(0);
			m_motorFRight.set(0);
			Timer.delay(3);
		}
	}

		/*
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}*/
	
	/**
	 * copy paste this if need 4 wheel drive 
	 * else if (driveChange == true){
			m_motorFLeft.set(speed);
			m_motorFRight.set(speed);
			m_motorBLeft.set(speed);
			m_motorBRight.set(speed);
			Timer.delay(3);
			m_motorFLeft.set(speed);
			m_motorFRight.set(-speed);
			m_motorBLeft.set(speed);
			m_motorBRight.set(-speed);
			Timer.delay(3);
			m_motorFLeft.set(0);
			m_motorFRight.set(0);
			m_motorBLeft.set(0);
			m_motorBRight.set(0);
			Timer.delay(3);
		}
	 */
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		ai.getAverageBits(); 
		ai.getVoltage();
		if(m_xboxcontroller.getX(Hand.kLeft)<= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != -1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6 + m_xboxcontroller.getX(Hand.kLeft)*-.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)>= .1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != 1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6 + m_xboxcontroller.getX(Hand.kLeft)*.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)<= .1 && m_xboxcontroller.getX(Hand.kLeft)>= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.3 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*-.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.3 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*-.6);
		}else if(m_xboxcontroller.getTriggerAxis(Hand.kRight)< .1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)< .1){
			m_motorFLeft.set(0);	
			m_motorFRight.set(0);
		}else if(m_xboxcontroller.getX(Hand.kLeft)<= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != -1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6 + m_xboxcontroller.getX(Hand.kLeft)*.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)>= .1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != 1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6 + m_xboxcontroller.getX(Hand.kLeft)*-.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)<= .1 && m_xboxcontroller.getX(Hand.kLeft)>= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*.6);
		}else if(m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1){
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*.6);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*-.6);
		}
		/**
		Rtrigger = (m_xboxcontroller.getTriggerAxis(Hand.kRight));
		if(Rtrigger == 1);{
			togglegas = true;
		}
		if(Rtrigger == 0){
			togglegas = false;
		}
		
		 //right trigger toggle
		 
		
		 
		 if(m_xboxcontroller.getYButtonPressed()){
			 if(toggleY == false){
				 toggleY = true;
			 }else if(toggleY == true){
				 toggleY = false;
			 }
			 //Y button toggle
		 }
		 
		 
		 if(toggleY == false && togglegas == false && driveChange == false){
			 m_motorFLeft.set(m_xboxcontroller.getY(Hand.kLeft)* .5);	
			 m_motorFRight.set(m_xboxcontroller.getY(Hand.kRight)* .5);
			 
		 }else if(toggleY == true && togglegas == false && driveChange == false){
			 m_motorFLeft.set(m_xboxcontroller.getY(Hand.kLeft)*-.5);
			 m_motorFRight.set(m_xboxcontroller.getY(Hand.kRight)*-.5);
			 
		 }else if (togglegas == true){
			 m_intake.set(m_xboxcontroller.getX(Hand.kLeft));	
			 m_intake2.set(m_xboxcontroller.getX(Hand.kRight));
			 
		 }else if (toggleY == false && togglegas == false && driveChange == true){
			 m_motorFLeft.set(m_xboxcontroller.getY(Hand.kLeft)* .5);	
			 m_motorFRight.set(m_xboxcontroller.getY(Hand.kRight)* .5);
			 m_motorBLeft.set(m_xboxcontroller.getY(Hand.kLeft)* .5);
			 m_motorBRight.set(m_xboxcontroller.getY(Hand.kRight)* .5);
			 
		 }else if (toggleY == true && togglegas == false && driveChange == true){
			 m_motorFLeft.set(m_xboxcontroller.getY(Hand.kLeft)*-.5);
			 m_motorFRight.set(m_xboxcontroller.getY(Hand.kRight)*-.5);
			 m_motorBLeft.set(m_xboxcontroller.getY(Hand.kLeft)*-.5);
			 m_motorBRight.set(m_xboxcontroller.getY(Hand.kRight)*-.5);
		 }*/
		 }
		// inputs for motors from Xbox Controller
		



	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
