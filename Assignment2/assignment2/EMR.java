package assignment2;

import java.io.*;
import java.util.*;

import assignment2.Patient.Insurance;

/* A basic command line interface for an Electronic Medical Record System.
 * 
 * The simplest way to complete this assignment is to perform 1 functionality at a time. Start
 * with the code for the EMR constructor to import all data and then perform tasks 1-10
 * 		1.	Add a new patient to the EMR system
 *  	2.	Add a new Doctor to the EMR system
 *  	3.	Record new patient visit to the department
 *  	4.	Edit patient information
 *  	5.	Display list of all Patient IDs
 *  	6.	Display list of all Doctor IDs
 *  	7.	Print a Doctor's record
 *  	8.	Print a Patient's record
 *  	9.	Exit and save modifications
 * 	
 *	Complete the code provided as part of the assignment package. Fill in the \\TODO sections
 *  
 *  Do not change any of the function signatures. However, you can write additional helper functions 
 *  and test functions if you want.
 *  
 *  Do not define any new classes. Do not import any data structures. Do not call the sort functions
 *  of ArrayList class. Implement your own sorting functions and implement your own search function.
 *  
 *  Make sure your entire solution is in this file.
 *  
 *  We have simplified the task of reading the data from the Excel files. Instead of reading directly
 *  from Excel, each Sheet of the Excel file is saved as a comma separated file (csv) 
 * 
 */


public class EMR
{
	private String aDoctorFilePath;
	private String aPatientFilePath;
	private String aVisitsFilePath;
	private ArrayList<Doctor> doctorList;
	private ArrayList<Patient> patientList;
	
	/**
     * Used to invoke the EMR command line interface. You only need to change
     * the 3 filepaths.
	 */
	public static void main(String[] args) throws IOException
	{
		EMR system = new EMR("./Data/Doctors.csv", "./Data/Patients.csv", "./Data/Visits.csv");
		system.displayMenu();
	}
	
	
	/**
	 * You don't have to modify the constructor, nor its code
	 * @param pDoctorFilePath
	 * @param pPatientFilePath
	 * @param pVisitsFilePath
	 */
	public EMR(String pDoctorFilePath, String pPatientFilePath, String pVisitsFilePath){
		this.aDoctorFilePath = pDoctorFilePath;
		this.aPatientFilePath = pPatientFilePath;
		this.aVisitsFilePath = pVisitsFilePath;
		
		importDoctorsInfo(this.aDoctorFilePath);
		importPatientInfo(this.aPatientFilePath);
		importVisitData(this.aVisitsFilePath);
		
		sortDoctors(this.doctorList);
		sortPatients(this.patientList);
	}

	/**
	 * This method should sort the doctorList in time O(n^2). It should sort the Doctors
	 * based on their ID 
	 */
	private void sortDoctors(ArrayList<Doctor> docs){
		ArrayList<Doctor> unsorted = docs;
		ArrayList<Doctor> sorted = new ArrayList <Doctor>();
		long least, current, next;
		Doctor cDoc, nDoc;
		Doctor lDoc = unsorted.get(0);
		int index = 0;

			for (int i = 0; i < unsorted.size(); i++){
				i = 0;
				cDoc = unsorted.get(i);
				current = cDoc.getID();
				least = current;
				for (int j = 0; j < unsorted.size(); j++){
					nDoc = unsorted.get(j);
					next = nDoc.getID();
					if (next < least) least = next;
				}
				lDoc = findDoctor(least);
				sorted.add(index, lDoc);
				unsorted.remove(lDoc);
				unsorted.trimToSize();
				if (unsorted.isEmpty()) return;
				lDoc = unsorted.get(0);
				index++;
			}
			lDoc = doctorList.get(doctorList.indexOf(lDoc));
			sorted.add(index, lDoc);
			doctorList.clear();
			doctorList.addAll(sorted);
		}
	
	/**
	 * This method should sort the patientList in time O(n log n). It should sort the 
	 * patients based on the hospitalID
	 */
	private void sortPatients(ArrayList<Patient> patients){
		if (patients.size() <= 1) return;

		ArrayList<Patient> unsorted = patients;
		ArrayList<Patient> sorted = new ArrayList<Patient>();
		ArrayList<Patient> left = new ArrayList<Patient>();
		ArrayList<Patient> right = new ArrayList<Patient>();
		int half = patients.size()/2;
		Patient middle = unsorted.get(half);
		String middleID = middle.getHospitalID();
		Patient current;
		String currentID;

		right.add(middle);
		for (int i = 0; i < patients.size(); i++){
			current = unsorted.get(i);
			currentID = current.getHospitalID();
			if (currentID.compareTo(middleID) < 0) left.add(current); 
			if (currentID.compareTo(middleID) > 0) right.add(current);
		}
		sortPatients(left);
		sortPatients(right);
		sorted = left;
		sorted.addAll(right);
		
		patients.clear();
		patients.addAll(sorted);
	}
	
	/**
	 * This method adds takes in the path of the Doctor sheet csv file and imports
	 * all doctors data into the doctorList ArrayList
	 */
	private ArrayList<Doctor> importDoctorsInfo(String doctorFilePath){
		doctorList = new ArrayList<Doctor>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(doctorFilePath));
			String currentLine = reader.readLine();
			while (currentLine != null){
				String[]drStats = currentLine.split(",", 4);
				if(!drStats[0].equalsIgnoreCase("firstname")){
					String first = drStats[0].trim();
					String last = drStats[1].trim();
					String spec = drStats[2].trim();
					long ID = Long.parseLong(drStats[3].trim());
					Doctor newDoc = new Doctor(first, last, spec, ID);
					doctorList.add(newDoc);
				}
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (IOException error){
			System.out.println("Sorry, cannot be completed due to the following error: " + error);
		}
		sortDoctors(doctorList);
		return doctorList;
	}
	
	/**
	 * This method adds takes in the path of the Patient sheet csv file and imports
	 * all Patient data into the patientList ArrayList
	 */
	private ArrayList<Patient> importPatientInfo(String patientFilePath){
		patientList = new ArrayList<Patient>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(patientFilePath));
			String currentLine = reader.readLine();
			while (currentLine !=null){
				String[]patStats = currentLine.split(",", 7);
				if(!patStats[0].equalsIgnoreCase("firstname")){
					String first = patStats[0].trim();
					String last = patStats[1].trim();
					double height = Double.parseDouble(patStats[2]);
					String gender = patStats[4].trim();
					String ins = patStats[3].trim();
					Patient.Insurance insurance = Patient.Insurance.NONE;
					if (Patient.Insurance.RAMQ.toString().equalsIgnoreCase(ins)) insurance = Patient.Insurance.RAMQ;
					else if (Patient.Insurance.Private.toString().equalsIgnoreCase(ins)) insurance = Patient.Insurance.Private;
					long HospitalID = Long.parseLong(patStats[5].trim());
					String DOB = patStats[6].trim();
					Patient newPat = new Patient(first, last, height, gender, insurance, HospitalID, DOB);
					patientList.add(newPat);
				}
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (IOException error){
			System.out.println("Sorry, cannot be completed due to the following error: " + error);
		}
		sortPatients(patientList);
		return patientList;
	}
	
	/**
	 * This method adds takes in the path of the Visit sheet csv file and imports
	 * every Visit data. It appends Visit objects to their respective Patient
	 */
	private void importVisitData(String visitsFilePath){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(visitsFilePath));
			String currentLine = reader.readLine();
			while (currentLine != null){
				String[]visDetail = currentLine.split(",", 4);
				Patient patient = null;
				Doctor doc = null;
				if (!visDetail[0].equalsIgnoreCase("HospitalID")){
					String hID = visDetail[0].trim();
						int pat = 0;
						while ((!patientList.get(pat).getHospitalID().matches(hID)) && (patientList.get(pat) != null)) pat++;
						patient = patientList.get(pat); 
					Long drID = Long.parseLong(visDetail[1].trim());
					doc = findDoctor(drID);
					String date = visDetail[2].trim();
					String note = visDetail[3];
					Visit newVis = new Visit(doc, patient, date, note);
					patient.aVisitList.add(newVis);
				}
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (IOException error){
			System.out.println("Sorry, cannot be completed due to the following error: " + error);
		}
	}
	
	/**
	 * This method uses an infinite loop to simulate the interface of the EMR system.
	 * A user should be able to select 10 options. The loop terminates when a user 
	 * chooses option 10: EXIT. You do not have to modify this code.
	 */
	public void displayMenu(){
		System.out.println();
		System.out.println("****************************************************************");
		System.out.println();
		System.out.println("Welcome to The Royal Victoria EMR Interface V1.0");
		System.out.println("");
		System.out.println("This system will allow you to access and modify the health records of the hospital");
		System.out.println();
		System.out.println("****************************************************************");
		System.out.println();
		
		Scanner scan = new Scanner(System.in);
		boolean exit = false;
		int choice = 0;
		
		while (!exit){
			System.out.println("Please select one of the following options and click enter:");
			System.out.println("   (1) Add a new patient to the EMR system\n" +
								"   (2) Add a new Doctor to the EMR system\n" +
								"   (3) Record new patient visit to the department\n" +
								"   (4) Edit patient information\n" +
								"   (5) Display list of all Patient IDs\n" +
								"   (6) Display list of all Doctor IDs\n" +
								"   (7) Print a Doctor's record\n" +
								"   (8) Print a Patient's record\n" +
								"   (9) Exit and save modifications\n");
			System.out.print("   ENTER YOUR SELECTION HERE: ");
			
			//int choice = 0;
			try{
				choice = Integer.parseInt(scan.next());
			}
			catch(Exception e){
				;
			}
			
			System.out.println("\n");
			
			switch(choice){
				case 1: 
					option1();
					break;
				case 2: 
					option2();
					break;
				case 3: 
					option3();
					break;
				case 4: 
					option4();
					break;
				case 5: 
					option5();
					break;
				case 6: 
					option6();
					break;
				case 7: 
					option7();
					break;
				case 8: 
					option8();
					break;
				case 9: 
					option9();
					break;	
				default:
					System.out.println("   *** ERROR: You entered an invalid input, please try again ***\n");
			}
			//choice = Integer.parseInt(scan.next());
			scan.close();
		}
	}
	
	/**
	 * This method adds a patient to the end of the patientList ArrayList. It 
	 * should ask the user to provide all the input to create a Patient object. The 
	 * user should not be able to enter empty values. The input should be supplied
	 * to the addPatient method
	 */
	private void option1(){
		//Ask the user to supply by command-line values for all the variables below.
		String firstname = null;
		String lastname = null;
		double height = 0;
		String Gender = null;
		String insurance = null;
		Patient.Insurance type = null;
		Long hospitalID = null;
		String DOB = null;
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("In order to add a new patient to the system, you must first input the following details... ");
		System.out.print("   -> First name: " );
		firstname = keyboard.next();
		System.out.print("   -> Last name: ");
		lastname = keyboard.next();
		System.out.print("   -> Height(cm): ");
		height = Double.parseDouble(keyboard.next());
		System.out.print("   -> Gender(F/M): ");
		Gender = keyboard.next();
		if (Gender.equalsIgnoreCase("f")) Gender = "Female";
		if (Gender.equalsIgnoreCase("m")) Gender = "Male";
		System.out.print("   -> Insurance: ");
		insurance = keyboard.next();
		type = Patient.Insurance.NONE;
		if (type.equals(Patient.Insurance.valueOf(insurance))) type = Patient.Insurance.valueOf(insurance);
		System.out.print("   -> Date of birth (mm-dd-yyyy): \n");
		System.out.print("      + Month: ");
		String mm = keyboard.next();
		System.out.print("      + Day: ");
		String dd = keyboard.next();
		System.out.print("      + Year: ");
		String yyyy = keyboard.next();
		DOB = mm + "-" + dd + "-" + yyyy;
		keyboard.reset();
		keyboard.close();
		hospitalID = Long.parseLong(patientList.get((patientList.size()-1)).getHospitalID()+1);
		System.out.print("   -> Hospital ID: " + hospitalID);
		
		addPatient(firstname, lastname, height, Gender, type, hospitalID, DOB);
		return;
	}
	
	/**
	 * This method adds a patient object to the end of the patientList ArrayList. 
	 */
	private void addPatient(String firstname, String lastname, double height, String Gender, Patient.Insurance type, Long hospitalID, String DOB){
		Patient newPat = new Patient(firstname, lastname, height, Gender, type, hospitalID, DOB);
		patientList.add(newPat);
		System.out.println("\n" + "Thank you, " + firstname + " " + lastname + " has been added to the system.");
		return;
	}
	
	
	/**
	 * This method adds a doctor to the end of the doctorList ArrayList. It 
	 * should ask the user to provide all the input to create a Doctor object. The 
	 * user should not be able to enter empty values.
	 */
	private void option2(){
		//Ask the user to supply by command-line values for all the variables below.
		String firstname = null;
		String lastname = null;
		String specialty = null;
		Long doctor_id = null;
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("In order to add a new doctor to the system, you must first input the following details... ");
		System.out.print("   -> First name: " );
		firstname = keyboard.next();
		System.out.print("   -> Last name: ");
		lastname = keyboard.next();
		System.out.print("   -> Specialty: ");
		specialty = keyboard.next();
		doctor_id = doctorList.get((doctorList.size()-1)).getID()+1;
		System.out.print("   -> Doctor ID: " + doctor_id);
		keyboard.close();
		
		addDoctor(firstname, lastname, specialty, doctor_id);
	}
	
	/**
	 * This method adds a doctor to the end of the doctorList ArrayList.
	 */
	private void addDoctor(String firstname, String lastname, String specialty, Long docID){
		Doctor newDoc = new Doctor(firstname, lastname, specialty, docID);
		doctorList.add(newDoc);
		System.out.println("\n" + "Thank you, " + firstname + " " + lastname + " has been added to the system.");
		return;
	}
	
	/**
	 * This method creates a Visit record. 
	 */
	private void option3(){
		//Ask the user to supply by command-line values for all the variables below.
		Long doctorID = null;
		Long patientID = null;
		String date = null;
		String note = null;
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Please input the following details... ");
		System.out.print("Doctor ID: ");
		doctorID = keyboard.nextLong();
		System.out.print("\n" + "Patient ID: ");
		patientID = keyboard.nextLong();
		System.out.print("\n" + "Date of visit: ");
		date = keyboard.next();
		System.out.print("\n" + "Additional notes: ");
		note = keyboard.next();
		keyboard.close();
		
		//Use above variables to find which Doctor the patient saw
		Doctor d = doctorList.get(doctorList.indexOf(doctorID));
		Patient p = patientList.get(patientList.indexOf(patientID));
		
		recordPatientVisit(d, p, date, note);
	}
	
	/**
	 * This method creates a Visit record. It adds the Visit to a Patient object.
	 */
	private void recordPatientVisit(Doctor doctor, Patient patient, String date, String note){
		Visit newVis = new Visit(doctor, patient, date, note);
		patient.aVisitList.add(newVis);
		String name = patient.getFirstName() + " " + patient.getLastName();
		System.out.println("\n" + "Thank you, " + name + "'s visit has been recorded.");
	}
	
	/**
	 * This method edits a Patient record. Only the firstname, lastname, height,
	 * Insurance type, and date of birth could be changed. You should ask the user to supply the input.
	 */
	private void option4(){
		// These are the 5 values that could change. You must ask the user to input new values 
		// for each of the 5 variables
		String newFirstname = null;
		String newLastname = null;
		double newHeight = 0;
		Patient.Insurance newType = null;
		String newDOB = null;
		
		Scanner keyboard = new Scanner(System.in);

		System.out.println("Please enter the Hospital ID of the patient you would like to edit...");
		Long patientID = keyboard.nextLong();
		
		System.out.println("Would you like to modify the patient's name? (yes/no)");
		String response = keyboard.next();
		while (!response.equalsIgnoreCase("no")){
			if (!response.equalsIgnoreCase("yes")){
				System.out.println("I'm sorry, `" + response + "` is not a valid response.");
				response = keyboard.next();
			}
			if (response.equalsIgnoreCase("yes")){
				System.out.print("   -> First name: ");
				newFirstname = keyboard.next();
				System.out.print("   -> Lirst name: ");
				newLastname = keyboard.next();
			}
		}
		response = null;

		System.out.println("Would you like to change the patient's height? (yes/no)");
		response = keyboard.next();
		while (!response.equalsIgnoreCase("no")){
			if (!response.equalsIgnoreCase("yes")){
				System.out.println("I'm sorry, `" + response + "` is not a valid response.");
				response = keyboard.next();
			}
			if (response.equalsIgnoreCase("yes")){
				System.out.print("   -> Height (cm): ");
				newHeight = keyboard.nextDouble();
			}
		}
		response = null;

		System.out.println("Would you like to update the patient's insurance? (yes/no)");
		response = keyboard.next();
		while (!response.equalsIgnoreCase("no")){
			if (!response.equalsIgnoreCase("yes")){
				System.out.println("I'm sorry, `" + response + "` is not a valid response.");
				response = keyboard.next();
			}
			if (response.equalsIgnoreCase("yes")){
				System.out.print("   -> Insurance: ");
				newType = Patient.Insurance.valueOf(keyboard.next());
			}
		}
		response = null;

		System.out.println("Would you like to fix the patient's birthday? (yes/no)");
		response = keyboard.next();
		while (!response.equalsIgnoreCase("no")){
			if (!response.equalsIgnoreCase("yes")){
				System.out.println("I'm sorry, `" + response + "` is not a valid response.");
				response = keyboard.next();
			}
			if (response.equalsIgnoreCase("yes")){
				System.out.print("   -> Date of birth (mm-dd-yyyy): \n");
				System.out.print("      + Month: ");
				String mm = keyboard.next();
				System.out.print("      + Day: ");
				String dd = keyboard.next();
				System.out.print("      + Year: ");
				String yyyy = keyboard.next();
				newDOB = mm + "-" + dd + "-" + yyyy;
			}
		}
		keyboard.close();
		
		editPatient(patientID, newFirstname, newLastname, newHeight, newType, newDOB);
		return;
	}
	
	/**
	 * This method edits a Patient record. Only the firstname, lastname, height, 
	 * Insurance type, address could be changed, and date of birth. 
	 */
	private void editPatient(Long hospitalID, String firstname, String lastname, double height, Patient.Insurance type, String DOB){
		Patient edited = patientList.get(patientList.indexOf(hospitalID));
		if (firstname != null) edited.setFirstName(firstname);
		if (lastname != null) edited.setLastName(lastname);
		if (height != 0) edited.setHeight(height);
		if (type != null) edited.setInsurance(type);
		if (DOB != null) edited.setDateOfBirth(DOB);
		
		System.out.println("\n" + "Thank you, " + firstname + "  " + lastname + "'s information has been updated.");
		return;
	}
	
	/**
	 * This method should first sort the patientList and then print to screen 
	 * one Patient at a time by calling the displayPatients() method
	 */
	private void option5(){
		sortPatients(patientList);
		displayPatients(patientList);
	}
	
	/**
	 * This method should print to screen 
	 * one Patient at a time by calling the Patient toString() method
	 */
	private void displayPatients(ArrayList<Patient> patients){
		Patient current;
		
		for (int i = 0; i < patients.size(); i++){
			current = patients.get(i);
			System.out.print(current.toString());
		}
	}
	
	/**
	 * This method should first sort the doctorList and then print to screen 
	 * one Doctor at a time by calling the displayDoctors() method
	 */
	private void option6(){
		sortDoctors(doctorList);
		displayDoctors(doctorList);
	}

	/**
	 * This method should first sort the doctorList and then print to screen 
	 * one Doctor at a time by calling the Doctor toString() method
	 */
	private void displayDoctors(ArrayList<Doctor> docs){
		sortDoctors(docs);
		Doctor current;
		
		for (int i = 0; i < docs.size(); i++){
			current = docs.get(i);
			System.out.print(current.toString());
		}
	}

	
	/**
	 * This method should ask the user to supply an id of a doctor they want info about
	 */
	private void option7(){
		// ask the user to specify the id of the doctor
		Long doc_id = null;
		
		Scanner keyboard = new Scanner (System.in);
		System.out.println("Please insert the ID of the doctor you wish to view...");
		doc_id = keyboard.nextLong();
		keyboard.close();
		
		Doctor d = findDoctor(doc_id);
		printDoctorRecord(d);
	}


	/**
	 * This method should call the toString method of a specific Patient. It should
	 * also list all the patient's Visit objects sorted in order by date (earliest first). For
	 * every Visit, the doctor's firstname, lastname and id should be printed as well.
	 */
	private void printPatientRecord(Long patientID){
		Patient cPat = patientList.get(patientList.indexOf(patientID));
		System.out.println(cPat.toString());
		
		ArrayList<Visit> sorted = new ArrayList<Visit>();
		int noVis = cPat.aVisitList.size();
		String vDay;
		String[]dates = new String[noVis];
		for (int i = 0; i < noVis; i++){
			vDay = cPat.aVisitList.get(i).getDate();
			dates[i] = vDay;
		}
		Arrays.sort(dates);
		Visit cDay;
		String cDate;
		for (int i = 0; i < noVis; i++){
			vDay = dates[i];
			for (int j = 0; j < noVis; j++){
				cDay = cPat.aVisitList.get(j);
				cDate = cDay.getDate();
			if (cDate.equals(vDay)) sorted.add(cDay);
			}
		}
		Doctor cDoc = null;
		for (int i = 0; i < sorted.size(); i++){
			cDoc = doctorList.get(doctorList.indexOf(sorted.get(i).getDoctor()));
			System.out.print(sorted.get(i).getDate() + ", "
			+ cDoc.getID() + ", "
			+ cDoc.getFirstName() + " " + cDoc.getLastName() + ", "
			+ sorted.get(i).getNote() + "\n");
		}
	}
	
	/**
	 * This method should ask the user to supply an id of the patient they want info about
	 */
	private void option8(){
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Please insert the ID of the patient you wish to view...");
		String id = keyboard.next();
		Long patientID = Long.parseLong(id);
		keyboard.close();
		
		printPatientRecord(patientID);
	}


	/**
	 * Searches in O(log n) time the doctorList to find the correct doctor with doctorID = id
	 * @param id
	 * @return
	 */
	private Doctor findDoctor(Long id){
		ArrayList<Doctor> unsearched = new ArrayList<Doctor>();
		unsearched.addAll(doctorList);
		ArrayList<Doctor> right = new ArrayList<Doctor>();
		ArrayList<Doctor> left = new ArrayList<Doctor>();
		Long guess;
		int half;
		Doctor middle;
		Doctor found = null;

		while ((!unsearched.isEmpty()) && (found == null)){ 
			half = unsearched.size()/2;
			middle = unsearched.get(half);
			guess = middle.getID();
			for (int i = 0; i < half; i++) left.add(unsearched.get(i));
			for (int i = half; i < unsearched.size(); i++) right.add(unsearched.get(i));

			if (guess.equals(id)) found = doctorList.get(doctorList.indexOf(middle));
			if (id.compareTo(guess) >= 0) unsearched.clear(); unsearched.addAll(right);
			if (id.compareTo(guess) < 0) unsearched.clear(); unsearched.addAll(left);
			unsearched.trimToSize();
			left.clear();
			right.clear();
		}
		//if (found == null) return unknown;
		return found;
	}
	
	/**
	 * This method should call the toString() method of a specific Doctor. It should
	 * also find and list all the patients that a Doctor has seen by calling their toString()
	 * method as well. It should also list the date that the doctor saw a particular patient
	 */
	private void printDoctorRecord(Doctor d){
		System.out.print(d.toString());

		Patient cPat;
		String date;
		for (int i = 0; i < patientList.size(); i++){
			cPat = patientList.get(i);
			if (cPat.aVisitList.contains(d)){
				System.out.print(cPat.toString());
				for (int j = 0; j < cPat.aVisitList.size(); j++){
					j = cPat.aVisitList.indexOf(d);
					date = cPat.aVisitList.get(j).getDate();
					System.out.print(date + "\n");
				}
			}
		}
	}

	/**
	 * This method should be invoked from the command line interface if the user
	 * would like to quit the program. This method should export all the Doctor, Patient and 
	 * Visit data by overwriting the contents of the 3 original files.
	 */
	private void option9(){
		exitAndSave();
	}
	
	
	/**
	 * Export all the Doctor, Patient and Visit data by overwriting the contents of the 3 original csv files.
	 */
	private void exitAndSave(){
		//TODO: Fill code here
	}

	
}

/**
 * This simple class just keeps the information about
 * a Patient together. You will have to Modify this class
 * and fill in missing data.
 */
class Patient
{
	public enum Insurance {RAMQ, Private, NONE};
	
	private String aFirstName;
	private String aLastName;
	private double aHeight;
	private String aGender;
	private Insurance aInsurance;
	private Long aHospitalID;
	private String aDateOfBirth; //ex. 12-31-1988 (Dec. 31st, 1988)
	ArrayList<Visit> aVisitList;
	
	public Patient(String pFirstName, String pLastName, double pHeight, String pGender, Insurance pInsurance,
			Long pHostpitalID, String pDateOfBirth)
	{
		this.aFirstName = pFirstName;
		this.aLastName = pLastName;
		this.aHeight = pHeight;
		this.aGender = pGender;
		this.aInsurance = pInsurance;
		this.aHospitalID = pHostpitalID;
		this.aDateOfBirth = pDateOfBirth;
		this.aVisitList = new ArrayList<Visit>();
	}
	
	public String getFirstName()
	{
		return aFirstName;
	}
	
	public String getLastName()
	{
		return aLastName;
	}

	public String getHospitalID()
	{
		String ID = Long.toString(aHospitalID);
		return ID;
	}

	public String getDateOfBirth()
	{
		return aDateOfBirth;
	}

	public void addVisit(String vDate, Doctor vDoctor){
		Patient vPatient = new Patient(aFirstName, aLastName, aHeight, aGender, aInsurance, aHospitalID, aDateOfBirth);
		String vNote = "";
		Visit newVisit = new Visit(vDoctor, vPatient, vDate, vNote);
		aVisitList.add(newVisit);
	}
	
	public void setFirstName(String fname){
		this.aFirstName = fname;
	}
	
	public void setLastName(String lname){
		this.aLastName = lname;
	}
	
	public void setHeight(double height){
		this.aHeight = height;
	}
	
	public void setInsurance(Insurance type){
		this.aInsurance = type;
	}
	
//	public void setInsurance(String type){
//		// Using overloading in order to make enum Insurance accessible from outside of Patient
//		Insurance insType = Insurance.valueOf(type);
//		this.aInsurance = insType;
//	}
	
	public void setDateOfBirth(String dob){
		this.aDateOfBirth = dob;
	}

	/**
	 * This method should print all the Patient's info. "ID, Lastname, Firstname, etc..."
	 */
	public String toString(){
		String patString = aHospitalID + ", " 
				+ aLastName + ", " 
				+ aFirstName +  ","
				+ aInsurance + ", " 
				+ aGender + ", " 
				+ aHeight + ", " 
				+ aDateOfBirth + "\n"; 
		return patString;
	}
}

/**
 * This simple class just keeps the information about
 * a Doctor together. Do modify this class as needed.
 */
class Doctor
{
	private String aFirstName;
	private String aLastName;
	private String aSpecialty; 
	private Long aID;
	
	public Doctor(String pFirstName, String pLastName, String pSpecialty, Long ID)
	{
		this.aFirstName = pFirstName;
		this.aLastName = pLastName;
		this.aSpecialty = pSpecialty;
		this.aID = ID;
	}
	
	public String getFirstName()
	{
		return aFirstName;
	}
	
	public String getLastName()
	{
		return aLastName;
	}

	public String getSpecialty(){
		return aSpecialty;
	}

	public Long getID(){
		return aID;
	}
	
	/**
	 * This method should print all the Doctor's info. "ID, Lastname, Firstname, Specialty"
	 */
	public String toString(){
		String drString = aID + ", "
				+ aLastName + ", " 
				+ aFirstName + ", " 
				+ aSpecialty + "\n";
		return drString;
	}
}

/**
 * This simple class just keeps the information about
 * a Visit together. Do modify this class as needed.
 */
class Visit
{
	private Doctor aDoctor;
	private Patient aPatient;
	private String aDate; 
	private String anote;
	
	public Visit(Doctor vDoctor, Patient vPatient, String vDate, String vNote)
	{
		this.aDoctor = vDoctor;
		this.aPatient = vPatient;
		this.aDate = vDate;
		this.anote = vNote;
	}
	
	public Doctor getDoctor()
	{
		return aDoctor;
	}
	
	public Patient getPatient()
	{
		return aPatient;
	}

	public String getDate(){
		return aDate;
	}
	
	public String getNote(){
		return anote;
	}

}