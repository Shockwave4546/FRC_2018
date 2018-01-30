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
// Everyone - 1/17/18 -- 1/23/18: Working on minor things and getting people caught up
// Everyone - 1/24/18--1/27/18: Working on getting the code cleaned up and set up better for final robot instead of the test robot

package org.usfirst.frc.team4546.robot;



import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



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
	private SpeedController m_motorTopRight;
	private static final int kIntakePort2 = 1;
	private SpeedController m_intake2;
	private static final int kMotorPort3 = 2;
	private SpeedController m_motorBottomRight;
	private static final int kIntakePort = 3;
	private SpeedController m_intake;
	private static final int kMotorPort4 = 4;
	private SpeedController m_motorBottomLeft;
	private static final int kMotorPort2 = 5;
	private SpeedController m_motorTopLeft;
	private static final int kArmMotor = 6;
	private SpeedController m_arm;
	
	

	private boolean toggleY = false;
	
	private double Rtrigger = 0;
  
	double LeftMotorValue;
	double RightMotorValue;


	
	private boolean togglegas = false;
	
	
	DigitalInput limitSwitch;
	
	public int x = 0;
	
	public AnalogInput ai;	
	
	@Override
	public void robotInit() {
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		m_PDP = new PowerDistributionPanel(kPDP);
		SmartDashboard.putData("Voltage", m_PDP);
		
		m_xboxcontroller = new XboxController(kJoystickPort);
		
		// Creates motor ports
		m_motorTopRight = new Talon(kMotorPort);
		m_motorBottomRight = new Talon(kMotorPort3);
		m_motorTopLeft = new Talon(kMotorPort2);
		m_motorTopLeft.setInverted(true);
		m_motorBottomLeft = new Talon(kMotorPort4);
		m_motorBottomLeft.setInverted(true);
		m_arm = new Talon(kArmMotor);
		
		
		m_intake = new Talon(kIntakePort);
		m_intake2 = new Talon(kIntakePort2);
		
		limitSwitch = new DigitalInput(0);
		
		ai = new AnalogInput(0);

		
		
		//boolean toggleY = false;//toggle variable for y button
	
		
		// Creates camera and video feed
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
			
			CvSink cvsink = CameraServer.getInstance().getVideo();
			CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
			
			Mat source = new Mat();
			Mat output = new Mat();
			
			while(!Thread.interrupted()) {
				cvsink.grabFrame(source);
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
				outputStream.putFrame(output);
			}
			
		}).start();

		
		
		
		//Inserts a blank textbox with true or false value (set to false first from Iterative function)
				SmartDashboard.putBoolean("TestToggle",false);
				
				//Inserts blank value in for the Left & Right Slider that can be modified to look like an slider.
				SmartDashboard.putNumber("LeftMotorSlider",0);
				SmartDashboard.putNumber("RightMotorSlider",0);
				
				//Allows for the current motor value to be shown on Shuffleboard.
				SmartDashboard.putNumber("LeftMotorValue",0);
				SmartDashboard.putNumber("RightMotorValue",0);
		



		//Toggle Button for Boolean on ShuffleBoard
				boolean togglevalue = SmartDashboard.getBoolean("TestToggle", false);
				if(togglevalue == true) {
				
						}else if(togglevalue == false) {
							
						}
						
				// Slider for the Left Motor and Right Motor (Gets value from slider)
				LeftMotorValue = SmartDashboard.getNumber("LeftMotorSlider",0);
				RightMotorValue = SmartDashboard.getNumber("RightMotorSlider",0);
				SmartDashboard.putNumber("LeftMotorValue",LeftMotorValue);
				SmartDashboard.putNumber("RightMotorValue",RightMotorValue);
						
				//Gets value from slider in previous lines and sets the motor value.
				m_motorTopLeft.set(LeftMotorValue);
				m_motorTopRight.set(RightMotorValue);
		
	}


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
		//figure out timer or delay command to make timed movements during auto
		
		m_motorTopRight.set(0.3);
		
		Timer.delay(1);
		
		m_motorTopRight.set(-0.3);
		
		Timer.delay(1);
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
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		//right trigger toggle gas
		 Rtrigger = (m_xboxcontroller.getTriggerAxis(Hand.kRight));
		 if(Rtrigger == 1);{
		 	togglegas = true;
		 }
		 if(Rtrigger == 0){
			togglegas = false;
		 }
		 //Y button toggle direction and xbox controller inputs
		 if(m_xboxcontroller.getYButtonPressed()){
			 if(toggleY == false){
				 toggleY = true;
			 }else if(toggleY == true){
				 toggleY = false;
					}
				}
		 
		 if(toggleY == false){
			 m_motorTopLeft.set(m_xboxcontroller.getY(Hand.kLeft)* .5);	
			 m_motorTopRight.set(m_xboxcontroller.getY(Hand.kRight)* .5);
			 m_motorBottomLeft.set(m_xboxcontroller.getY(Hand.kLeft)* .5);	
			 m_motorBottomRight.set(m_xboxcontroller.getY(Hand.kRight)* .5);
		 }else if(toggleY == true){
			 m_motorTopLeft.set(m_xboxcontroller.getY(Hand.kRight)*-.5);
			 m_motorTopRight.set(m_xboxcontroller.getY(Hand.kLeft)*-.5);
			 m_motorBottomLeft.set(m_xboxcontroller.getY(Hand.kRight)*-.5);
			 m_motorBottomRight.set(m_xboxcontroller.getY(Hand.kLeft)*-.5);
		 }
		 if (togglegas == true){
			 m_intake.set(m_xboxcontroller.getX(Hand.kLeft)*1/3);	
			 m_intake2.set(m_xboxcontroller.getX(Hand.kRight)*1/3);
		 }
		 if (togglegas == false){
			 m_intake.set(0);
			 m_intake2.set(0);			 
		 }
		
		 
		while(limitSwitch.get()){
			x = x + 1;
			System.out.println(x);
			m_motorTopRight.set(0); 
			m_motorTopLeft.set(0);
			m_motorBottomRight.set(0);
			m_motorBottomLeft.set(0);
			Timer.delay(0.5);
			
			
			ai.getAverageBits();
			ai.getVoltage();
		}
		
	
	
	}
//:)
		
		
	@Override
	public void testPeriodic() {
	}
}
