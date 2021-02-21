import java.io.*;
import java.util.*;
import java.sql.*;


public class AuthenticationSystem {
	
	//Scanner for string input
	public static String input() {
		String input;
		Scanner scnr = new Scanner(System.in);
		input = scnr.nextLine();
		return input;
	}
	
	//Scanner for integer inputs
	public static int intInput() {
		int input = -1;
		try {
			Scanner scnr = new Scanner(System.in);
			input = scnr.nextInt();
		}
		catch(RuntimeException ex){
			System.out.println("Invalid input");
		}
		return input;
	}

	public static void makeDatabase(Connection con) throws Exception{
		
		Class.forName("org.h2.Driver").getConstructor().newInstance();
		
	}
	
	
	public static void makeTable(Connection con, Statement stmt) {
		try
		{
			String sql =" CREATE TABLE IF NOT EXISTS EMPLOYEE"
					+  "(id INTEGER NOT NULL AUTO_INCREMENT,"
					+  " username VARCHAR(45) NOT NULL,"
					+  " password VARCHAR(45) NOT NULL,"
					+  " role VARCHAR(45) NOT NULL,"
					+  " PRIMARY KEY (id),"
					+  " UNIQUE KEY id_UNIQUE (username))";
			
			stmt.executeUpdate(sql);
			
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public static void addFirstUser(Connection con, Statement stmt) {
		try
		{
			String sql = "INSERT INTO employee (username, password, role)" + "VALUES ('samantha','SNHU','admin')";
			stmt.executeUpdate(sql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public static void addEmployee(Connection con, Statement stmt) throws SQLException {
		int unique = 0;
		System.out.println("Enter the employees username: ");
		String username = input();
		System.out.println("The new employee default password is password123 please inform the new hire of this information.");
		System.out.println("Enter the employees role. Type either admin or employee: ");
		String role = input();
		while(!(role.contentEquals("employee") ||(role.contentEquals("admin")))){
			System.out.println("Error please type either employee or admin");
			role = input();
		}
				
		//check for uniqueness
		String sql1 = "select * from EMPLOYEE";
		ResultSet rs = stmt.executeQuery(sql1);
		while(rs.next()) {
			if(rs.getString(2).contentEquals(username)) {
				System.out.println("An employee with that username already exists. Usernames must be unique.");
				break;
			}
			else {
				unique = 1;					
			}
		}
		
		if(unique == 1) {
			String sql = "INSERT INTO EMPLOYEE (username, password, role)" + "VALUES ('" + username + "', 'password123', '" + role + "' )";
			int rows = stmt.executeUpdate(sql);
			if(rows > 0) {
				System.out.println("A new employee has been created with the credentials: " + username + " " + "password123" + " " + role);
			}
		}
	}
	
	public static void deleteEmployee(Connection con, Statement stmt) throws SQLException {
		System.out.println("Please enter the username of the employee to be removed: ");
		String username = input();
		System.out.println("Deleting this user cannot be undone are you sure you want to delete this user? y/n: ");
		String yn = input();
		if(yn.contentEquals("y")||yn.contentEquals("Y")) {
			String sql = "delete from EMPLOYEE where username = '" + username + "';";
			stmt.executeUpdate(sql);
			System.out.println("Employee removed");
			
		}
		else {
			System.out.println("Employee not deleted.");
		}
		
	}
	
	public static void updateEmployeePassword(Connection con, Statement stmt, String password, int index) throws SQLException {
		String sql = "update EMPLOYEE"
				+ " set password = '" + password + "'"
				+ " where id = '" + index + "';";
		stmt.executeUpdate(sql);
		System.out.println("user updated");
		
	}
	
	//print employees
	public static void employeeInformation(Connection con, Statement stmt)  {
		try
		{
			String sql = "select id, username, role from EMPLOYEE";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}

		
	}
	
	
	//get user index
	public static int index(Connection con, Statement stmt, String username) throws SQLException {
		int index = -1;
		String sql = "select id from EMPLOYEE where username='" + username + "';" ;
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			index = rs.getInt(1);
		}
		return index;
	}
	
	//get user index
	public static String role(Connection con, Statement stmt, String username) throws SQLException {
		//set to employee by default to keep from accidently accessing admin menu
		String role = "employee";
		String sql = "select username, role from EMPLOYEE";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			String databaseUsername = rs.getString("username");
			String databaseRole = rs.getString("role");
			if(databaseUsername.contentEquals(username)) {
				role = databaseRole;
				break;
			}
		}
		
		return role;
	}
	
	
	//check user name and password for verification
	public static boolean verification(Connection con, Statement stmt, int index, String inputPass) throws IOException, SQLException{
		String password = "";
		//set to false to stop accidental access
		boolean verified = false;
		
		//get the password from the database
		String sql = "select password from EMPLOYEE where id='" + index + "';" ;
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			password = rs.getString(1);
		}
				
		if(inputPass.contentEquals(password)) {
			verified = true;
		}
				
		return verified;
		
	}
	
	//create user message files
	public static void createMessageFiles(Connection con, Statement stmt) throws SQLException {
		
		int i;
		
			try {
				
				String sql = "select * from EMPLOYEE";
				ResultSet rs = stmt.executeQuery(sql);
				while(rs.next()) {
					i = rs.getInt(1);
					String filename = i + ".txt";
					File messageFile = new File(filename);
					messageFile.createNewFile();
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	

	//read message file
	public static LinkedList<String> messageList(int index) throws FileNotFoundException{
		LinkedList<String> messages = new LinkedList<String>();
		String filename = index + ".txt";
		Scanner file = new Scanner(new File(filename));
		while(file.hasNext())
		{
			messages.add(file.nextLine());
		}
		file.close();
		
		return messages; 
	}
	
	//print message file
	public static void printMessages(LinkedList<String> messages) {
		for(int i = 0; i < messages.size(); i++) {
			System.out.println((i + 1) + ": " + messages.get(i));
		}
	}
	
	//delete message
	public static void deleteMessage(LinkedList<String> messages, int num, String yn) {
		if(yn.contentEquals("y") || yn.contentEquals("Y")) {
			num--;
			
			//check to see if message exists
			if(num < messages.size() && num >= 0) {
				messages.remove(num);
				System.out.println("Message Removed\n");
			}
			else {
				System.out.println("Error no message with that number exists");
			}

		}
		else if(yn == "n" || yn == "N") {
			System.out.println("Message not deleted.");
		}
		else 
		{
			System.out.println("Please type either y or n");
		}		
		
	}
	
	//save message file
	public static void saveMessages(LinkedList<String> messages, int index) throws IOException {
		String filename = index + ".txt";
		FileWriter writer = new FileWriter(filename);
		for(int i = 0; i < messages.size(); i++) {	
			writer.write(messages.get(i));
			writer.write(System.lineSeparator());
		}
		writer.close();
	}
	
	//print messages
	public static void printMessages(int index, LinkedList<String> messages) {
		int count = 1;
		for(int i = 0; i < messages.size(); i++) {
			count = count + i;
			System.out.println(count + ": " + messages.get(i));
		}
		
	}
	
	//add a message
	public static void sendMessage(LinkedList<String> sentMessages, String message) {
		sentMessages.add(message);
		System.out.println("Message Added\n"); 
		
		
	}
	
	//print employee options
	public static void employeeOptionsMenu() {
		System.out.println("Here are your options:");
		System.out.println("1. Check messages");
		System.out.println("2. Delete message");
		System.out.println("3. Send message");
		System.out.println("4. Change password");
		System.out.println("5. Quit");
	}
	
	//print admin options
	public static void adminOptionsMenu() {
		System.out.println("Here are your options:");
		System.out.println("1. Check messages");
		System.out.println("2. Delete message");
		System.out.println("3. Send message");
		System.out.println("4. Change password");
		System.out.println("5. Add new employee");
		System.out.println("6. Remove employee");
		System.out.println("7. View current employees");
		System.out.println("8. Quit");
	}
	
	public static void main(String[] args) throws Exception {

		String userName , password , newPassword, role, menuSelection = "", message = "", yn = "" ;
		int triesRemaining = 3;
		int index, messageUserIndex = -1, messageRemove = -1;
		boolean verified;
		LinkedList<String> messages = new LinkedList<String>();
		LinkedList<String> sentMessages = new LinkedList<String>();
		
		//create database
		Connection con = DriverManager.getConnection("jdbc:h2:" + "./Database/employeeinformation", "root", "mypassword");
		Statement stmt = con.createStatement();
		makeDatabase(con);
		makeTable(con, stmt);
		
		//check to see if there is an initial user
		String firstUser = "select * from EMPLOYEE";
		ResultSet rs1 = stmt.executeQuery(firstUser);
		if(!rs1.next()) {
			addFirstUser(con, stmt);
		}
			
		//create message files
		createMessageFiles(con, stmt);
		
		
		while(triesRemaining != 0) {
			
			//get user name
			System.out.println("Please Enter your username or type q to quit: ");
			userName = input();

			
			if(userName.contentEquals("q")|| userName.contentEquals("Q")) {
				System.out.println("Goodbye!");
				System.exit(0);;
			}

			System.out.println("Please Enter your Password: ");
			password = input();
			
			//get index for user information
			index = index(con, stmt, userName);
			
			
			if(index > -1) {
				verified = verification(con, stmt, index, password);
				
				//get user role
				role = role(con, stmt, userName);
				
				
				//check if they typed the right username and password
				if(verified == true) {

				System.out.println("Welcome " + userName);
				messages = messageList(index);
					
					if(role.contentEquals("employee")) {
						//employee options
						while(!(menuSelection.contentEquals("5"))) {
							
					
							//print menu and get selection
							employeeOptionsMenu();
							menuSelection = input();
							
							//check messages
							if(menuSelection.contentEquals("1")) {
								System.out.println("\nMessages:");
								messages = messageList(index);
								printMessages(messages);
								if(messages.size() == 0) {								
									System.out.println("There are no messages\n");
								}
								System.out.println();
							}
							
							//delete messages
							else if(menuSelection.contentEquals("2")) {
								System.out.println("Type the number of the message you would like to remove: ");
								messageRemove = intInput();
								System.out.println("Are you sure you would like to delete message: " + messageRemove + " y/n?");
								yn = input();
								deleteMessage(messages, messageRemove , yn); 	
								saveMessages(messages, index);
							}
							
							//send message
							else if(menuSelection.contentEquals("3")) {
								System.out.println("Enter the username of the person you would like to send a message to: ");
								userName = input();
								messageUserIndex = index(con, stmt, userName);
								if (messageUserIndex == -1) {
									System.out.println("No user with that username");
								}
								else {
									sentMessages = messageList(messageUserIndex);
									System.out.println("Type your message and then hit enter: ");
									message = input();
									sendMessage(sentMessages, message);
									saveMessages(sentMessages, messageUserIndex);
									
								}
							}
							
							//change password
							else if(menuSelection.contentEquals("4")) {
								System.out.println("\nEnter your new password:");
								newPassword = input();
								updateEmployeePassword(con, stmt, newPassword, index);
							}
							
							//quit
							else if(menuSelection.contentEquals("5")) {
								System.out.println("\nGoodbye!\n");
								System.exit(0);
							}
							
							//error handling
							else {
								System.out.println("Invalid selection. Please type 1 2 3 4 or 5.\n");
							}
						}
					}
					
					//admin options
					else if(role.contentEquals("admin")) {
						while(!(menuSelection.contentEquals("8"))) {

							//print menu and get selection
							adminOptionsMenu();
							menuSelection = input();
							
							//check messages			
							if(menuSelection.contentEquals("1")) {
								System.out.println("\nMessages:");
								messages = messageList(index);
								printMessages(messages);
								if(messages.size() == 0) {								
									System.out.println("There are no messages\n");
								}
								System.out.println();
							}
							//delete message
							else if(menuSelection.contentEquals("2")) {
								System.out.println("Type the number of the message you would like to remove: ");
								messageRemove = intInput();
								System.out.println("Are you sure you would like to delete message: " + messageRemove + " y/n?");
								yn = input();
								deleteMessage(messages, messageRemove , yn); 	
								saveMessages(messages, index);
							}
							
							//send message
							else if(menuSelection.contentEquals("3")) {
								System.out.println("Enter the username of the person you would like to send a message to: ");
								userName = input();
								messageUserIndex = index(con, stmt, userName);
								if (messageUserIndex == -1) {
									System.out.println("No user with that username");
								}
								else {
									sentMessages = messageList(messageUserIndex);
									System.out.println("Type your message and then hit enter: ");
									message = input();
									sendMessage(sentMessages, message);
									saveMessages(sentMessages, messageUserIndex);	
								}
								
							}
							
							
							//change password
							else if(menuSelection.contentEquals("4")) {
								System.out.println("\nEnter your new password:");
								newPassword = input();
								updateEmployeePassword(con, stmt, newPassword, index);
							}
							
							//add new employee
							else if(menuSelection.contentEquals("5")) {
								addEmployee(con, stmt);
								createMessageFiles(con, stmt);
							}
							
							//remove employee
							else if(menuSelection.contentEquals("6")) {
								System.out.println("Please enter the username of the employee to be removed: ");
								String username = input();
								System.out.println("Deleting this user cannot be undone are you sure you want to delete this user? y/n: ");
								String yn1 = input();
								int deleteIndex = index(con, stmt, username);
								File file = new File(deleteIndex + ".txt");
								if(yn1.contentEquals("y")||yn1.contentEquals("Y")) {
									String sql = "delete from EMPLOYEE where username = '" + username + "';";
									stmt.executeUpdate(sql);
									System.out.println("Employee removed");
									//delete their message file
									file.delete();
																	
								}
								else {
									System.out.println("Employee not deleted.");
								}
							}
							
							//show employee information
							else if(menuSelection.contentEquals("7")) {
								employeeInformation(con, stmt);
							}
							
							//quit
							else if(menuSelection.contentEquals("8")) {
								System.out.println("\nGoodbye!\n");
								System.exit(0);
							}
							
							//error handling
							else {
								System.out.println("Invalid selection please type 1-7\n");
							}
							
						}
					}
				}
				else {
					System.out.println("Incorrect login information");
					triesRemaining--;
					System.out.println(triesRemaining + " login attempts remaining");
					
				}
			
			}
		
		
			else {
				System.out.println("Incorrect login information");
				triesRemaining--;
				System.out.println(triesRemaining + " login attempts remaining");
			}
		}
		
	}
	
}
