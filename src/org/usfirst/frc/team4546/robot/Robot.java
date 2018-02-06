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
	private static final int kMotorPort2 = 2;
	private SpeedController m_motorFLeft;
	//Two motor drive
	
	private static final int kMotorPort3 = 5;
	private SpeedController m_motorBRight;
	private static final int kMotorPort4 = 4;
	private SpeedController m_motorBLeft;
	//extra motors for 4 wheel drive
	
	private SpeedController m_intake;
	private static final int kIntakePort = 3;	
	private SpeedController m_intake2;
	private static final int kIntakePort2 = 1;
	//intake motors
	
	private boolean driveChange = false;
	private AnalogInput ai;
	double speedF1 = .429;
	double speedFN1 = -.429;
	
	double speedF2 = .5;
	double speedFN2 = -.4;
	
	double speedB1 = .5;
	double speedBN1 = -.5;
	
	double speedB2 = .5;
	double speedBN2 = -.5;
	
	double speedI1 = 2/3;
	double speedI2 = 1/3;
	double LeftMotorValue;
	double RightMotorValue;
	
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
		m_motorBRight.setInverted(true);
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
		
		SmartDashboard.putBoolean("testToggle", false);
		SmartDashboard.putNumber("LeftMotorSlide", 0);
		SmartDashboard.putNumber("RightMotorSlider", 0);
		SmartDashboard.putNumber("LeftMotorValue", 0);
		SmartDashboard.putNumber("RightMotorValue", 0);
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
			m_motorFLeft.set(speedF1);
			m_motorFRight.set(speedF2);
			Timer.delay(3);
			m_motorFLeft.set(speedF1);
			m_motorFRight.set(speedFN2);
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
		if(m_xboxcontroller.getBumper(Hand.kRight)== true){
			m_intake.set(m_xboxcontroller.getX(Hand.kRight)*2/3);
			m_intake2.set(m_xboxcontroller.getX(Hand.kLeft)*1/3);
		}
			//box in take motors
		if(m_xboxcontroller.getY(Hand.kLeft)<=.1 && m_xboxcontroller.getY(Hand.kLeft)>=-.1 && m_xboxcontroller.getX(Hand.kLeft)<=.1 && m_xboxcontroller.getX(Hand.kLeft)>=-.1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Idle
			m_motorFLeft.set(0);	
			m_motorFRight.set(0);
			System.out.println("idle");
		}else if(m_xboxcontroller.getY(Hand.kLeft)== -1 && m_xboxcontroller.getX(Hand.kLeft)!= -1 && m_xboxcontroller.getX(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Forward
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
			System.out.println("forward");
		}else if(m_xboxcontroller.getY(Hand.kLeft)== 1 && m_xboxcontroller.getX(Hand.kLeft)!= -1 && m_xboxcontroller.getX(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Back
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
			System.out.println("back");
		}else if(m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getY(Hand.kLeft)!= -1 && m_xboxcontroller.getY(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Spin Left
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
			System.out.println("spin left");
		}else if(m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getY(Hand.kLeft)!= -1 && m_xboxcontroller.getY(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Spin Right
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
			System.out.println("spin right");
		}else if(m_xboxcontroller.getY(Hand.kLeft)== -1 && m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Forward Left
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
			System.out.println("forward left");
		}else if(m_xboxcontroller.getY(Hand.kLeft)== -1 && m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Forward Right
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
			System.out.println("forward right");
		}else if(m_xboxcontroller.getY(Hand.kLeft)== 1 && m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Back Left
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);
			System.out.println("Back left");
		}else if(m_xboxcontroller.getY(Hand.kLeft)== 1 && m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
			//Back Right
			m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
			m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
			System.out.println("Back right");
		}
		}
	/** Tank Drive
	if(m_xboxcontroller.getY(Hand.kLeft)<.1 && m_xboxcontroller.getY(Hand.kLeft)>-.1){
	m_motorFLeft.set(0);
}else if(m_xboxcontroller.getY(Hand.kLeft)>=.1){
	m_motorFLeft.set(m_xboxcontroller.getY(Hand.kLeft)*-speedF1);
}else if(m_xboxcontroller.getY(Hand.kLeft)<=-.1){
	m_motorFLeft.set(m_xboxcontroller.getY(Hand.kLeft)*-speedF1);
}

if(m_xboxcontroller.getY(Hand.kRight)<.1 && m_xboxcontroller.getY(Hand.kRight)>-.1){
	m_motorFRight.set(0);
}else if(m_xboxcontroller.getY(Hand.kRight)>=.1){
	m_motorFRight.set(m_xboxcontroller.getY(Hand.kRight)*-speedF2);
}else if(m_xboxcontroller.getY(Hand.kRight)<=-.1){
	m_motorFRight.set(m_xboxcontroller.getY(Hand.kRight)*-speedF2);
}*/
	/** One stick control
	if(m_xboxcontroller.getY(Hand.kLeft)<=.1 && m_xboxcontroller.getY(Hand.kLeft)>=-.1 && m_xboxcontroller.getX(Hand.kLeft)<=.1 && m_xboxcontroller.getX(Hand.kLeft)>=-.1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Idle
	m_motorFLeft.set(0);	
	m_motorFRight.set(0);
	System.out.println("idle");
}else if(m_xboxcontroller.getY(Hand.kLeft)== -1 && m_xboxcontroller.getX(Hand.kLeft)!= -1 && m_xboxcontroller.getX(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Forward
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	System.out.println("forward");
}else if(m_xboxcontroller.getY(Hand.kLeft)== 1 && m_xboxcontroller.getX(Hand.kLeft)!= -1 && m_xboxcontroller.getX(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Back
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
	System.out.println("back");
}else if(m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getY(Hand.kLeft)!= -1 && m_xboxcontroller.getY(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Spin Left
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	System.out.println("spin left");
}else if(m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getY(Hand.kLeft)!= -1 && m_xboxcontroller.getY(Hand.kLeft)!= 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Spin Right
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
	System.out.println("spin right");
}else if(m_xboxcontroller.getY(Hand.kLeft)== -1 && m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Forward Left
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	System.out.println("forward left");
}else if(m_xboxcontroller.getY(Hand.kLeft)== -1 && m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Forward Right
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	System.out.println("forward right");
}else if(m_xboxcontroller.getY(Hand.kLeft)== 1 && m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Back Left
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);
	System.out.println("Back left");
}else if(m_xboxcontroller.getY(Hand.kLeft)== 1 && m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getBumper(Hand.kRight)== false){
	//Back Right
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1 + m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
	System.out.println("Back right");
}*/
	/** Racing Controls
	if(m_xboxcontroller.getX(Hand.kLeft)<= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != -1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)* speedF1);
	m_motorBLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)* speedF1);
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2 + m_xboxcontroller.getX(Hand.kLeft)*speedFN2);
	m_motorBRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2 + m_xboxcontroller.getX(Hand.kLeft)*speedFN2);
	//if right trigger is pressed, right motor speeds up based on negative value of the left joystick X axis
	
}else if(m_xboxcontroller.getX(Hand.kLeft)>= .1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != 1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1 + m_xboxcontroller.getX(Hand.kLeft)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	//if right trigger is pressed, left motor speeds up based on positive value of the left joystick X axis
	
}else if(m_xboxcontroller.getX(Hand.kLeft)<= .1 && m_xboxcontroller.getX(Hand.kLeft)>= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	//if right trigger is pressed, moves straight if left joystick X axis is between -.1 and .1
	
}else if(m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF2);
	//if right trigger is pressed, and X axis of left joystick is held at 1,  spins to the left
	
}else if(m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)>=.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kRight)*speedFN2);
	//if right trigger is pressed, and X axis of left joystick is held at -1,  spins to the right
	
}else if(m_xboxcontroller.getTriggerAxis(Hand.kRight)< .1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)< .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(0);	
	m_motorFRight.set(0);
	//motors stop when triggers are not held down
	
}else if(m_xboxcontroller.getX(Hand.kLeft)<= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != -1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN1 + m_xboxcontroller.getX(Hand.kLeft)*speedF1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN2);
	//if left trigger is pressed, left motor speeds up based on negative value of the left joystick X axis
	
}else if(m_xboxcontroller.getX(Hand.kLeft)>= .1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getX(Hand.kLeft) != 1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN2 + m_xboxcontroller.getX(Hand.kLeft)*speedFN2);
	//if left trigger is pressed, right motor speeds up based on positive value of the left joystick X axis
	
}else if(m_xboxcontroller.getX(Hand.kLeft)<= .1 && m_xboxcontroller.getX(Hand.kLeft)>= -.1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN2);
	//if left trigger is pressed, moves straight if left joystick X axis is between -.1 and .1
	
}else if(m_xboxcontroller.getX(Hand.kLeft)== -1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedF2);
	//if left trigger is pressed, and X axis of left joystick is held at 1,  spins to the left
	
}else if(m_xboxcontroller.getX(Hand.kLeft)== 1 && m_xboxcontroller.getTriggerAxis(Hand.kLeft)>=.1 && m_xboxcontroller.getTriggerAxis(Hand.kRight)<= .1 && m_xboxcontroller.getBumper(Hand.kRight)!= true){
	m_motorFLeft.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN1);	
	m_motorFRight.set(m_xboxcontroller.getTriggerAxis(Hand.kLeft)*speedFN2);
	//if left trigger is pressed, and X axis of left joystick is held at -1,  spins to the right
}*/
	/**if(ai.getValue() >= 3050){
			m_motorFLeft.set(0.3);
		}
		else if(ai.getValue() <= 3000 && ai.getValue() >= 2000){
			m_motorFLeft.set(0.1);
		}
		else if(ai.getValue() <= 1950){
			m_motorFLeft.set(0);
		}
		boolean togglevalue = SmartDashboard.getBoolean("TestToggle", false);
		if(togglevalue == true){
			
		}else if(togglevalue == false){
			
		}
		
		LeftMotorValue = SmartDashboard.getNumber("LeftMotorSlider", 0);
		RightMotorValue = SmartDashboard.getNumber("RightMotorSlider", 0);
		SmartDashboard.putNumber("LeftMotorValue", LeftMotorValue);
		SmartDashboard.putNumber("RightMotorValue", RightMotorValue);
	}*/
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
