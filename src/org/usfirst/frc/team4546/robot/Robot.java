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
// DP,SC - 1/19/18: Working on inverting motors with toggle of the Y Button
// DP,GFB,SC - 1/20/18: Finished the inverting of motors, and GFB began graphing robot voltages


package org.usfirst.frc.team4546.robot;

//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;
//import edu.wpi.cscore.CvSink;
//import edu.wpi.cscore.CvSource;
//import edu.wpi.cscore.UsbCamera;
//import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
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
	private static final int kJoystickPort = 0;
	//private Joystick m_joystick;
	private XboxController m_xboxcontroller;
	private static final int kMotorPort = 0;
	private SpeedController m_motorRight;
	private static final int kMotorPort2 = 5;
	private SpeedController m_motorLeft;
	private boolean toggleY = false;
	

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
		//m_joystick = new Joystick(kJoystickPort);3
		m_xboxcontroller = new XboxController(kJoystickPort);
		// Creates motor ports
		m_motorRight = new Talon(kMotorPort);
		m_motorLeft = new Talon(kMotorPort2);
		m_motorLeft.setInverted(true);
		
		//boolean toggleY = false;//toggle variable for y button
	
		/*
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
		*/
		
		
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
		//m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		//System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		double speed = .2;
		m_motorLeft.set(speed);
		m_motorRight.set(speed);
		/*switch (m_autoSelected) {
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
		m_PDP.getVoltage();
		SmartDashboard.putData("Voltage",m_PDP);
		/*Gets voltage and puts the integer retrieve on the SmartDashboard*/
		m_PDP.getTotalCurrent();
		SmartDashboard.putData("Total Current",m_PDP);
		/*Gets Total Current and puts Current of all PDP channels on SmartDashboard*/
		 
		// Setting up the joysticks on the Xbox controller
		if(m_xboxcontroller.getXButtonPressed()){
			System.out.println("This is the X Button on the Xbox Controller");
		}
		/*test for X Button (Returns "This is the X Button on the Xbox Controller")*/
		
		if(m_xboxcontroller.getYButtonPressed()){
			if(toggleY == false){
				toggleY = true;
			}else if(toggleY == true){
				toggleY = false;
			}
		}
		/*test for Y Button (Returns "This is the Y Button on the Xbox Controller")*/
		
		if(m_xboxcontroller.getAButtonPressed()){
			System.out.println("This is the A Button on the Xbox Controller");
		}
		/*test for A Button (Returns "This is the A Button on the Xbox Controller")*/
		
		if(m_xboxcontroller.getBButtonPressed()){
			System.out.println("This is the B Button on the Xbox Controller");
		}
		/*test for B Button (Returns "This is the B Button on the Xbox Controller")*/
	
		
		
		
		/*            Invert Motor Direction on Y Axis                       */
		if(toggleY == false){
			m_motorLeft.set(m_xboxcontroller.getY(Hand.kLeft)* -1);	
			m_motorRight.set(m_xboxcontroller.getY(Hand.kRight)* -1);
		}else if(toggleY == true){
			m_motorLeft.set(m_xboxcontroller.getY(Hand.kLeft));
			m_motorRight.set(m_xboxcontroller.getY(Hand.kRight));
		}
	}
	// use this for inputs of buttons
		//if(m_xboxcontroller.getYButtonPressed()){



	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
