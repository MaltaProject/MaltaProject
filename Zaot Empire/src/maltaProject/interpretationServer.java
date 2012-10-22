package maltaProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;


/**
 * This class obtains user input. It then analyses the input and chooses the appropriate action.
 *
 * @author  Nicholas Ingalls
 */


//--- TODO Section ---
//TODO ***KILL CHAT THREAD WHEN USER LOGS OUT***
//TODO Redo Fight Code
//TODO Add some sort of examine code for inv items
//TODO Finish drop()
//TODO start take()
//TODO Make skills() command. This command would print the skills a character currently has
//TODO make learn() command. This command would print the skills that a character can gain. This information is stored in the profession folder under skills
//TODO make some exits require an object.
//TODO Create chat
//finish Yell (To Room and rooms surrounding), Tell, Say? (To room)
//OOC Green, IC purple, Yell red, tell yellow
//TODO Give() command
//TODO Make health/hp bar color depending on percentage. (50+ green)(25+ white)(15+ yellow)(0+ red)
//TODO when user exits it currently wipes who file instead of removing single entry.
//TODO *** Timer needs to work in a new thread or else it screws up the users location within the program. This is evident from the inability to utilize the wizard dialogue and also from the healthbar menu.


public class interpretationServer extends Thread{
	//---Called Classes---//
	setColor setColor = new setColor();

	//---Database Location---//
	String database = null; //Database address is passed to interpretationServer() from TelnetServer(). To modify path edit TelnetServer()

	//---Telnet Socket---//
	public Socket socket;
	public BufferedReader in;
	public PrintWriter out;

	//---Character Variables---//
	static String user = "";
	String age = "<none>";
	String race = "<none>";
	String location = "<none>";
	String name = "<none>";
	int hp = 0;
	int maxHp = 0;
	int xp = 0;
	int maxXp = 0;

	String list = "";
	String[] result;
	ArrayList<String> itemList = new ArrayList<String>(), shopList = new ArrayList<String>();
	String[] itemListTemp;
	static int numberofNPC = 0;
	boolean sleep = false; //True if the user is sleeping
	String strLine; //String from the scanner class - AKA user input
	String direction = "";
	String northRoom, eastRoom, southRoom, westRoom, upRoom, downRoom; //The program loads all rooms adjacent to the users current position.
	static int numofItems;
	boolean show = true; //decides whether to show the intro screen. For example if a password is incorrect, the intro screen will not be shown.
	boolean startup = true;
	boolean north = false, south = false, east = false, west = false, up = false, down = false;	
	public void ZaosClient(Socket s, String databaseLoc) throws IOException {
		database = databaseLoc;
		socket = s;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// Enable auto-flush:
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		// If any of the above calls throw an exception, the caller is responsible for closing the socket. Otherwise the thread will close it.
		start(); // Calls run()
	}	
	public void run() {
		login();
	}
	public void userCreator(){//Allows for new users to create an account
		String str = null;
		Scanner usrTxt = new Scanner(in);
		String strLine = "";
		out.println("Welcome to the User Creator");
		out.println("First you must pick a username. This username will be the name of your character so it must be appropriate to a role playing game.");
		boolean createUser = true;
		boolean matchPass = true;
		boolean error = false;
		String strPass="";
		while (createUser==true){
			error = false;
			out.println("");
			out.println("Username:");
			str = usrTxt.nextLine();
			str = str.trim();
			str = str.toLowerCase();
			str=str.replace("\n", "");
			user = str;
			try{
				FileInputStream fstream = new FileInputStream(database + "/login/"+user);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				strLine = br.readLine();
				in.close();
			} catch (Exception e){
				createUser = false;
			}
			if (createUser == true){
				out.println("");
				out.println("I'm afraid that username has already been taken.");
			}
		}

		while (matchPass == true){
			out.println("");
			out.println("Password:");

			strPass = usrTxt.nextLine();
			str = str.trim();
			str = str.toLowerCase();
			str=str.replace("\n", "");
			user = str;

			out.println("");
			out.println("");
			out.println("Re-Type Password:");
			String strPassChk = usrTxt.nextLine();
			str = str.trim();
			str = str.toLowerCase();
			str=str.replace("\n", "");

			if (strPass.equals(strPassChk)){
				matchPass = false;
			} else {
				out.println("I'm afraid those passwords don't match! Please try it again.");
			}
		}
		out.println("");
		out.println("");
		out.println("Adding an email acount will allow the administrators to contact you in the event of a problem or future updates. Although this is optional it is recommended.");
		out.println("If you do not want to provide an email address please just press enter.");
		out.println("");
		out.println("Email:");
		str = usrTxt.nextLine();
		str = str.trim();
		str = str.toLowerCase();
		str=str.replace("\n", "");

		try {
			FileWriter fstream = new FileWriter(database + "/login/"+user);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(strPass+"\n");
			out.write(str);
			out.close();
		}catch (Exception e){
		}

		out.println("");
		out.println("Your Profile has been sucessfully created. Please login to continue.");
		out.println("");
	}
	public void login(){//Account Login Page

		if (startup == true){
			out.println ((char)27 + "[2J");
			out.println ((char)27 + "[0m");
			startup = false;
		}
		if (show==true){
			show = false;
			out.println("");
			try{
				FileInputStream fstream = new FileInputStream(database + "/interface/welcome");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));	

				while ((strLine = br.readLine()) != null){
					setColor setColor = new setColor();
					strLine = setColor.color(strLine);
					out.println(strLine);
				}
				br.close();
			} catch (Exception e){
				System.out.println("An incoming user can't access welcome interface");
				logging("An incoming user can't access welcome interface");
			}
		}

		String str;
		Scanner usrTxt = new Scanner(in);
		String strLine = "";
		boolean failLogin = false;
		boolean login = false;
		while (login==false) {  
			out.println();
			out.println("p to reprint options");
			out.println("Select an Option:");
			str = usrTxt.nextLine();
			str = str.trim();
			str = str.toLowerCase();
			if (str.equals("q")){ 
				out.println("We only part to meet again.");
				System.out.println("<Control> - " + user + " has left the game.");
				logging("<Control> - " + user + " has left the game.");
				try {
					socket.close();
				} catch(IOException e) {
					System.out.println("<ERROR> - Could not close connection with user");
					logging("<ERROR> - Could not close connection with user");
				}
			} else if (str.equals("r")){
				userCreator();
			} else if (str.equals("p")){
				show = true;
				login();
			} else if (str.equals("l")){
				out.println("");
				out.println("");
				out.println("Username:");
				str = usrTxt.nextLine();
				str = str.trim();
				str = str.toLowerCase();
				str=str.replace("\n", "");
				user = str;
				System.out.println("<Control> - " + user + " is attempting to login");
				logging("<Control> - " + user + " is attempting to login");
				try{
					FileInputStream fstream = new FileInputStream(database + "/login/"+user);
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					strLine = br.readLine();
					in.close();
				}catch (Exception e){
					out.println("<Control> I'm afraid I can't find that user. Have you created an account yet?");
					out.println("<Control> If you need assistance you can speak to an admin @ nicholas.ingalls@gmail.com");
					failLogin = true;
					show = false;
					login();
				}
				if (failLogin==false){
					out.println("");
					out.println("");
					out.println("Password:" + (char)27 + "[8m");				//This ansi code will make the password the user types invisible
					str = usrTxt.nextLine();
					str = str.trim();
					str = str.toLowerCase();
					if (str.equals(strLine)){
						out.println((char)27 + "[0mLogin Sucessful!");			//Ansi reset
						login = true;
						System.out.println("<Control> - " + user + " has sucessfully logged in.");
						logging("<Control> - " + user + " has sucessfully logged in.");
						characterLogin();
					} else {
						out.println((char)27 + "[0mIncorrect!");
						System.out.println("<Control> - " + user + " has FAILED login.");
						logging("<Control> - " + user + " has FAILED login.");
						failLogin = true;
						show = false;
						login();
					}
				}
			}
		}

	}
	public void characterLogin(){//Character Login Page
		String strLine;
		boolean loadChar = false;
		int charLen = 0;
		boolean error = true;
		String space = " ";
		setColor setColor = new setColor();
		out.println("''''''''''''''''''''''''''''''''''''''''''''''''");
		out.println("'              Character Selection             '");
		out.println("'                                              '");
		out.println("' Character:                                   '");

		try{
			FileInputStream fstream = new FileInputStream(database + "/characters/"+user);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while(loadChar==false){
				strLine = br.readLine();
				charLen = strLine.length();
				charLen = 46 - charLen;
				while (charLen>3){
					space = space.concat(" ");
					charLen--;
				}

				out.println("' ~" + strLine + space +"'");
				space = " ";
				error = false;
			}
			in.close();
		} catch (Exception e){
			if (error==true){
				out.println("");
				out.println("You account looks like it is suffering against data lose! An admin has been notified and we will endeavor to fix it as quickly as possible. Please also drop me a line at nicholas.ingalls@gmail.com");
				out.println("");
				System.out.println("<ERROR> - "+user+" was unable to load any of his character profiles.");
				logging("<ERROR> - "+user+" was unable to load any of his character profiles.");
			}
		}

		out.println("'                                              '");
		out.println(setColor.color("' ([blue]P[white])lay a current Character.                  '"));
		out.println(setColor.color("' ([blue]C[white])reate a new Character                     '"));
		out.println(setColor.color("' ([blue]R[white])eprint this dialogue                      '"));
		out.println(setColor.color("' ([blue]L[white])ogout                                     '"));
		out.println("''''''''''''''''''''''''''''''''''''''''''''''''");
		out.println("");
		out.println("");
		out.println("Select an Option:");
		Scanner usrTxt = new Scanner(in);
		String str;
		str = usrTxt.nextLine();
		str = str.trim();
		str = str.toLowerCase();

		if (str.equals("c")){
			out.println("Character Creation:");
			out.println("Name: " + name);
			out.println("Race: " + race);
			out.println("Age: " + age);
			out.println("");
			out.println("Pick a (n)ame");
			out.println("Pick a (r)ace");
			out.println("Choose an (a)ge");
			out.println("(D)one - create my character");
			out.println("(A)bort character creation");
			out.println("");
			out.println("NOT CURRENTLY OPERATIONAL");
			characterLogin();
		} else if (str.equals("p")){
			try{
				BufferedWriter bW = new BufferedWriter(new FileWriter(database + "/who", true));
				bW.write(user);
				bW.newLine();
				bW.close();
			}catch(IOException e){
				e.printStackTrace();
			}

			//---Starts Chat Server--//
			startChat SC = new startChat(socket, database, user);

			room();
			/**
			out.println("");
			out.println("Name of the Character that you would like to play:");
			str = usrTxt.nextLine();
			str = str.trim();
			str = str.toLowerCase();
			try{
				FileInputStream fstream = new FileInputStream(database + "/characters/"+user);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while(loadChar==false){
					while (name.equals("<none>")){
						strLine = br.readLine();
						if (strLine.equals(str)){
							name = strLine;
						}
					}
				}
				in.close();
			} catch (Exception e){
				out.println("That isn't the name of one of your characters!");
			}
			 **/
		} else if (str.equals("r")){
			characterLogin();
		} else if (str.equals("l")){
			show = true;
			login();
		} else {
			out.println("That is not an option\n");
			characterLogin();
		}
	}
	public void room(){//Prints Description and exits of a room - called by interpretUsr()
		numberofNPC = 0;
		list = "";
		UserValue getValue = new UserValue(database,user);
		location = getValue.getLoc();
		System.out.println("<Control> - " + user + " has entered " + location);
		logging("<Control> - " + user + " has entered " + location);

		RoomValue RV = new RoomValue(database, location);
		String Title = RV.getTitle();
		String desc = RV.getDesc();
		northRoom = RV.getNorth();
		southRoom = RV.getSouth();
		eastRoom = RV.getEast();
		westRoom = RV.getWest();
		upRoom = RV.getUp();
		downRoom = RV.getDown();

		out.println((char)27 + "[34m" + "---" + Title + "---" + (char)27 + "[0m"); //Prints the title of the room
		out.println("");

		int len = 60;
		if (desc.length()<len){
			out.println(desc);
		} else {
			char [] chars = desc.toCharArray();
			Vector lines = new Vector();
			StringBuffer line = new StringBuffer();
			StringBuffer word = new StringBuffer();

			for (int i = 0; i < chars.length; i++) {
				word.append(chars[i]);

				if (chars[i] == ' ') {
					if ((line.length() + word.length()) > len) {
						lines.add(line.toString());
						line.delete(0, line.length());
					}

					line.append(word);
					word.delete(0, word.length());
				}
			}

			// handle any extra chars in current word
			if (word.length() > 0) {
				if ((line.length() + word.length()) > len) {
					lines.add(line.toString());
					line.delete(0, line.length());
				}
				line.append(word);
			}

			// handle extra line
			if (line.length() > 0) {
				lines.add(line.toString());
			}

			String [] ret = new String[lines.size()];
			int c = 0; // counter
			for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {
				ret[c] = (String) e.nextElement();
			}
			
			int length = ret.length;
			int current = 0;
			while (length>current+1){
				out.println(ret[current]);
				current++;
			}
		}
		String exits = listExit(location);
		out.println((char)27 + "[34m" + exits + (char)27 + "[0m");

		NPC();
		objects();
		interpretUsr();
	}
	public void NPC(){//Prints NPCs in a room - called by room();
		boolean error = false;
		boolean checkList = true;
		String checkNPC = "";
		try{
			FileInputStream fstream = new FileInputStream(database + "/rooms/"+location+"/npc");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));	
			while (error == false){
				strLine = br.readLine();
				result = strLine.split(":");
				try{
					checkNPC = result[numberofNPC];
					checkNPC = checkNPC.replace("[", "");
					checkNPC = checkNPC.replace("]", "");
					checkNPC = checkNPC.replace("\n", "");
					FileInputStream fstream2 = new FileInputStream(database + "/npc/"+checkNPC+"/"+checkNPC+".lst");
					DataInputStream in2 = new DataInputStream(fstream2);
					BufferedReader br2 = new BufferedReader(new InputStreamReader(in));	
					in2.close();
				} catch (Exception e){
					System.out.println("ERROR:"+result[numberofNPC]);
					logging("ERROR:"+result[numberofNPC]);
					checkList = false;
				}
				if (checkList==true){
					list = checkNPC;		
				} else {
					checkList = true;
				}
				numberofNPC=numberofNPC+2;
			}
			in.close();
		} catch (Exception e){
			error = true;
		}
		int currentNPC = 0;
		if (numberofNPC != 0){
			while (numberofNPC > currentNPC){
				out.println("["+result[currentNPC]+"] " + result[currentNPC+1]);
				currentNPC = currentNPC+2;
			}
		}
	}
	public void objects(){//Prints objects in a room - called by room();
		boolean error = false;
		int numofItems = 0;
		int getItemsList = 0;
		error = false;

		try{
			FileInputStream fstream = new FileInputStream(database + "/rooms/" + location + "/obj");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (true){
				strLine = br.readLine();
				itemListTemp = strLine.split(":");
				itemList.add(itemListTemp[1]);
				itemList.add(itemListTemp[0]);
				numofItems=numofItems+2;
			}
		} catch (Exception e){
			error = true;
		}
		error = false;

		try {
			while (true){
				if (numofItems==getItemsList){
					break;
				}
				out.println("[" + itemList.get(getItemsList+1)+ "] - " + itemList.get(getItemsList));
				getItemsList = getItemsList + 2;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void interpretUsr(){//User Input
		UserInterface UI = new UserInterface(database, user);
		out.println(UI.showHealthbar());
		String str = UI.getInput(in);
		String token = null;
		try {
			StringTokenizer st = new StringTokenizer(str);
			token = st.nextToken();
		} catch (Exception e1) {
			//Usually means the user just hit enter if there is an error here.
		}

		if (sleep==true) {
			if (str.equals("wake")){
				out.println("You wake up");
				sleep = false;
				interpretUsr();
			} else {
				out.println("You can't do that while you are sleeping!");
				interpretUsr();
			}
		} else {
			if (str.equals("n") | str.equals("north")) {
				if (northRoom.equals("none")){
					out.println("You can't go that way!");
					interpretUsr();
				} else {
					File bkup = new File(database + "/charProfile/" + user + "/location");
					bkup.delete();
					try {
						FileWriter fstream = new FileWriter(database
								+ "/charProfile/" + user + "/location");
						BufferedWriter writeout = new BufferedWriter(fstream);
						writeout.write(northRoom);

						try {
							FileInputStream fstream2 = new FileInputStream(
									database + "/rooms/" + location + "/"
											+ location + ".n");
							DataInputStream in = new DataInputStream(fstream2);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							out.println();
							out.println(br.readLine());
							out.println();
							br.close();
						} catch (Exception e) {
							out.println();
							out.println("You walk to the north.");
							out.println();
						}

						writeout.close();
					} catch (Exception e) {

					}
					room();
				}
			} else if (str.equals("e") | str.equals("e")) {
				if (eastRoom.equals("none")){
					out.println("You can't go that way!");
					interpretUsr();
				} else {


					File bkup = new File(database + "/charProfile/" + user + "/location");
					bkup.delete();
					try {
						FileWriter fstream = new FileWriter(database + "/charProfile/" + user + "/location");
						BufferedWriter writeout = new BufferedWriter(fstream);
						writeout.write(eastRoom);
						try {
							FileInputStream fstream2 = new FileInputStream(database + "/rooms/" + location + "/" + location + ".e");
							DataInputStream in = new DataInputStream(fstream2);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							out.println();
							out.println(br.readLine());
							out.println();
							br.close();
						} catch (Exception e) {
							out.println();
							out.println("You walk to the east.");
							out.println();
						}

						writeout.close();
					} catch (Exception e) {

					}
					room();
				}
			} else if (str.equals("s") | str.equals("south")) {
				if (southRoom.equals("none")){
					out.println("You can't go that way!");
					interpretUsr();
				} else {
					File bkup = new File(database + "/charProfile/" + user
							+ "/location");
					bkup.delete();
					try {
						FileWriter fstream = new FileWriter(database
								+ "/charProfile/" + user + "/location");
						BufferedWriter writeout = new BufferedWriter(fstream);
						writeout.write(southRoom);
						try {
							FileInputStream fstream2 = new FileInputStream(database
									+ "/rooms/" + location + "/" + location
									+ ".s");
							DataInputStream in = new DataInputStream(fstream2);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							out.println();
							out.println(br.readLine());
							out.println();
							br.close();
						} catch (Exception e) {
							out.println();
							out.println("You walk to the south.");
							out.println();
						}
						writeout.close();
					} catch (Exception e) {

					}
					room();
				}
			} else if (str.equals("w") | str.equals("west")) {
				if (westRoom.equals("none")){
					out.println("You can't go that way!");
					interpretUsr();
				} else {
					File bkup = new File(database + "/charProfile/" + user
							+ "/location");
					bkup.delete();
					try {
						FileWriter fstream = new FileWriter(database
								+ "/charProfile/" + user + "/location");
						BufferedWriter writeout = new BufferedWriter(fstream);
						writeout.write(westRoom);
						try {
							FileInputStream fstream2 = new FileInputStream(database
									+ "/rooms/" + location + "/" + location
									+ ".w");
							DataInputStream in = new DataInputStream(fstream2);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							out.println();
							out.println(br.readLine());
							out.println();
							br.close();
						} catch (Exception e) {
							out.println();
							out.println("You walk to the west.");
							out.println();
						}
						writeout.close();
					} catch (Exception e) {

					}
					room();
				}
			} else if (str.equals("d") | str.equals("down")) {
				if (downRoom.equals("none")){
					out.println("You can't go that way!");
					interpretUsr();
				} else {
					File bkup = new File(database + "/charProfile/" + user
							+ "/location");
					bkup.delete();
					try {
						FileWriter fstream = new FileWriter(database
								+ "/charProfile/" + user + "/location");
						BufferedWriter writeout = new BufferedWriter(fstream);
						writeout.write(downRoom);

						try {
							FileInputStream fstream2 = new FileInputStream(database
									+ "/rooms/" + location + "/" + location
									+ ".d");
							DataInputStream in = new DataInputStream(fstream2);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							out.println();
							out.println(br.readLine());
							out.println();
							br.close();
						} catch (Exception e) {
							out.println();
							out.println("You climb down.");
							out.println();
						}

						writeout.close();
					} catch (Exception e) {

					}
					room();
				}
			} else if (str.equals("u") | str.equals("up")) {
				if (upRoom.equals("none")){
					out.println("You can't go that way!");
					interpretUsr();
				} else {
					File bkup = new File(database + "/charProfile/" + user
							+ "/location");
					bkup.delete();
					try {
						FileWriter fstream = new FileWriter(database
								+ "/charProfile/" + user + "/location");
						BufferedWriter writeout = new BufferedWriter(fstream);
						writeout.write(upRoom);

						try {
							FileInputStream fstream2 = new FileInputStream(database
									+ "/rooms/" + location + "/" + location
									+ ".u");
							DataInputStream in = new DataInputStream(fstream2);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							out.println();
							out.println(br.readLine());
							out.println();
							br.close();
						} catch (Exception e) {
							out.println();
							out.println("You climb up.");
							out.println();
						}

						writeout.close();
					} catch (Exception e) {

					}
					room();
				}
			} else if (str.contains("help")) {
				str.replace("help ", "");
				help(str);
				interpretUsr();
			} else if (str.contains("jump")) {
				out.println("You jump up and down on the spot.");
				interpretUsr();
			} else if (str.contains("kiss")) {
				out.println("Your advances would not be appreciated!");
				interpretUsr();
			} else if (str.equals("l") | str.contains("look") | str.equals("")) {
				room();
			} else if (str.equals("list") | str.contains("buy ")) {
				try {
					list(str);
				} catch (IOException e) {
					System.out.println("<ERROR> " + user
							+ " can not access the shop list found in " + list);
					logging("<ERROR> " + user
							+ " can not access the shop list found in " + list);
				}
				interpretUsr();
			} else if (str.contains("fight ")) {
				String character = str;
				character = character.replace("fight ", "");
				fight(character);
			} else if (str.contains("examine ")) {
				String examine = str;
				examine = examine.replace("examine ", "");
				examine(examine);
			} else if (str.contains("quit")) {
				quit();
			} else if (str.equals("inventory") | str.equals("i")
					| str.equals("inv")) {
				inv();
				interpretUsr();
			} else if (str.contains("take ")) {
				take();
				interpretUsr();
			} else if (str.contains("drop ")) {
				drop(str);
				interpretUsr();
			} else if (token.equals("ooc")){
				sendChat SC = new sendChat(database, user);
				SC.ooc(str);
				interpretUsr();
			} else if (token.equals("ic")){
				sendChat SC = new sendChat(database, user);
				SC.ic(str);
				interpretUsr();
			} else if (str.equals("gold")) {
				gold();
				interpretUsr();
			} else if (str.equals("exit")){
				exit();
				interpretUsr();
			} else if (str.equals("wizard")|str.equals("wiz")) {
				wizardMenu();
			} else if (str.equals("sleep")){
				sleep();
			} else if (str.equals("database")) {
				//databaseManager DM = new databaseManager();
				//DM.start();
			} else {
				out.println("Sorry, I can't do that!");
				interpretUsr();
			}
		}

	}
	public void list(String str) throws IOException{ //Lists the items in the store and also controls the buy command
		boolean buyList = false;					//If true user is buying, if false user is listing
		if (str.contains("buy ")){
			buyList = true;
		}
		String shop = "";
		ArrayList<String> shopList = new ArrayList<String>();
		String[] shopListTemp;
		boolean error = false;
		int numofItems = 0;
		int getItemsList = 0;
		error = false;

		try{
			FileInputStream fstream = new FileInputStream(database + "/npc/"+list+"/"+list+".lst");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (true){
				strLine = br.readLine();
				shopListTemp = strLine.split(":");
				shopList.add(shopListTemp[1]);
				strLine = br.readLine();
				shopListTemp = strLine.split(":");
				shopList.add(shopListTemp[1]);
				numofItems=numofItems+2;
			}
		} catch (Exception e){
			error = true;
		}
		error = false;

		if (buyList==false){
			out.println("Cost   Item");
			try {
				while (true){
					out.println(shopList.get(getItemsList+1)+ " " + shopList.get(getItemsList));
					getItemsList = getItemsList + 2;
					if (numofItems==getItemsList){
						break;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			str = str.replace("buy ", "");
			str = str.toLowerCase();
			try {
				int loc = 0;
				int gld = 0, price = 0;
				while (true){
					shop = shopList.get(loc);
					shop = shop.toLowerCase();
					if (shop.equals(str)){
						UserValue getValue = new UserValue(database,user);
						gld = Integer.parseInt(getValue.getGold());
						price = Integer.parseInt(shopList.get(loc + 1));
						if (gld>price){
							File file = new File(database + "/charProfile/"+user+"/gold");
							file.delete();
							try {
								FileWriter fstream = new FileWriter(database + "/charProfile/"+user+"/gold");
								BufferedWriter out = new BufferedWriter(fstream);
								gld = gld - price;
								out.write("" + gld);
								out.close();
							}catch (Exception e){
							}
							try{
								// Create file 
								FileWriter fstream = new FileWriter(database + "/charProfile/"+user+"/inventory",true);
								BufferedWriter out = new BufferedWriter(fstream);
								out.write("1:"+str + "\n");
								//Close the output stream
								out.close();
							}catch (Exception e){//Catch exception if any
								System.err.println("Error: " + e.getMessage());
								logging("Error: " + e.getMessage());
							}
							out.println(str+" has been added to your inventory.");
							break;
						} else {
							out.println("You don't have that much money!");
							break;
						}
					}
					loc = loc + 2;
				}
			} catch (Exception e) {
				out.println("That item isn't offered in this store!");
			}

		}
	}
	public void exit(){ //Prints a list of exits for the specific room
		UserValue getValue = new UserValue(database, user);
		location = getValue.getLoc();
		String direction = listExit(location);
		if (direction.equals("")){
			out.print("There are no exits to this room.");
		} else {
		out.println("There are exits to the:");
		out.println((char)27 + "[34m" + direction + (char)27 + "[0m");
		}
	}
	public String listExit(String location){
		String direction = "";
		if (northRoom.equals("none")){
		} else {
			north = true;
			direction = direction + "[North] ";
		}
		if (southRoom.equals("none")){
		} else {
			south = true;
			direction = direction + "[South] ";
		}
		if (eastRoom.equals("none")){
		} else {
			east = true;
			direction = direction + "[East] ";
		}
		if (westRoom.equals("none")){
		} else {
			west = true;
			direction = direction + "[West] ";
		}
		if (upRoom.equals("none")){
		} else {
			up = true;
			direction = direction + "[Up] ";
		}
		if (downRoom.equals("none")){
		} else {
			down = true;
			direction = direction + "[Down] ";
		}

		return direction;	
	}	
	public void gold(){//Prints the amount of gold that a user has in their possession
		UserValue getValue = new UserValue(database, user);
		String gold = getValue.getGold();
		out.println("You currently have: " + gold);
		interpretUsr();
	}
	public void examine(String examine){// Prints out a more detailed description of an item described in a room description
		String[] examineTemp;
		String testExamine = "";
		try{
			FileInputStream fstream = new FileInputStream(database + "/rooms/"+location+"/examine");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (true){
				strLine = br.readLine();
				examineTemp = strLine.split(":");
				testExamine = examineTemp[0];

				if (testExamine.equals(examine)){
					break;
				}
			}
			out.println(examineTemp[1]);
			in.close();
		} catch (Exception e){
			out.println("I'm afraid there isn't anything by that name to examine!");
		}

		interpretUsr();
	}
	public void sleep(){ //TODO increases the rate at which health is regenerated by *3
		//TODO Sleep increases the rate at which health is regenerated.
		sleep = true;
		out.println("You lay down and fall into a deep sleep.");
		interpretUsr();
	}
	public void rest(){ //TODO increases the rate at which health is regenerated by *1.5

	}
	public void score(){ //prints out a formatted table of stats
		//TODO Have a nicely formated table that prints out lvl, hp, etc.
		UserValue getValue = new UserValue(database, user);
		String level = getValue.getLvl();

		System.out.println("|-----------------------User-----------------------|");
		System.out.println("| Name: " + user);
		System.out.println("| Level: " + level);
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");
		System.out.println("|");

		interpretUsr();
	}
	public void fight(String character){ //TODO Needs to be completely redone as /object directory was changed
		boolean continueOn = false;
		String characterActual = "", hurtDesc = "", killDesc = "", userWeapon = "", NPCweapon = "", userHurtdesc = "", userKilldesc = "";
		int NPCaccuracy = 0, userAccuracy = 0, userMindamage = 0, userMaxdamage = 0, maxDamage = 0, minDamage = 0;
		String[] characterSplit;

		try{
			FileInputStream fstream = new FileInputStream(database + "/rooms/"+location+"/npc");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (true){
				strLine = br.readLine();
				characterSplit = strLine.split(":");
				characterActual = characterSplit[0];
				strLine = characterSplit[0];
				strLine = strLine.toLowerCase();
				strLine = strLine.replace("-", " ");
				character = character.toLowerCase();
				character = character.replace("-", " ");

				if (strLine.contains(character)){
					continueOn = true;
					break;
				}
			}
			in.close();
		} catch (Exception e){
			out.println("You can't fight someone if they aren't in the room with you!");
		}
		if (continueOn==true){
			try{
				FileInputStream fstream = new FileInputStream(database + "/charProfile/" + user + "/wield");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while(true){
					strLine = br.readLine();
					if (strLine.contains("[right-hand:")){
						strLine = strLine.replace("[right-hand:", "");
						strLine = strLine.replace("]", "");
						userWeapon = strLine;
						break;
					}
				}
				in.close();
			} catch (Exception e){
				System.out.println("<ERROR> " + user + " was unable to access  their wield file");
				logging("<ERROR> " + user + " was unable to access  their wield file");
			}
			try{
				FileInputStream fstream = new FileInputStream(database + "/npc/" + characterActual + "/" + characterActual + ".inv");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while(true){
					strLine = br.readLine();
					if (strLine.contains("[right-hand:")){
						strLine = strLine.replace("[right-hand:", "");
						strLine = strLine.replace("]", "");
						NPCweapon = strLine;
						break;
					}
				}
				in.close();
			} catch (Exception e){
				System.out.println("<ERROR> " + user + " was unable to access NPC " + characterActual + " inv file");
				logging("<ERROR> " + user + " was unable to access NPC " + characterActual + " inv file");
			}
			try{
				FileInputStream fstream = new FileInputStream(database + "/objects/weapons/"+NPCweapon);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while(true){
					strLine = br.readLine();
					if (strLine.contains("minDamage:")){
						strLine = strLine.replace("minDamage:", "");
						minDamage = Integer.parseInt(strLine);
					} else if (strLine.contains("maxDamage:")){
						strLine = strLine.replace("maxDamage:", "");
						maxDamage = Integer.parseInt(strLine);
					} else if (strLine.contains("hurtDesc:")){
						strLine = strLine.replace("hurtDesc:", "");
						hurtDesc = strLine;
					} else if (strLine.contains("killDesc:")){
						strLine = strLine.replace("killDesc:", "");
						killDesc = strLine;
						break;
					} else if (strLine.contains("accuracy:")){
						strLine = strLine.replace("accuracy:", "");
						NPCaccuracy = Integer.parseInt(strLine);	
					}
				}
				in.close();
			} catch (Exception e){
				System.out.println("<ERROR> " + user + " was unable to access obj " + characterActual + " file");
				logging("<ERROR> " + user + " was unable to access obj " + characterActual + " file");
			}

			try{
				FileInputStream fstream = new FileInputStream(database + "/objects/weapons/"+userWeapon);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while(true){
					strLine = br.readLine();
					if (strLine.contains("minDamage:")){
						strLine = strLine.replace("minDamage:", "");
						userMindamage = Integer.parseInt(strLine);
					} else if (strLine.contains("maxDamage:")){
						strLine = strLine.replace("maxDamage:", "");
						userMaxdamage = Integer.parseInt(strLine);
					} else if (strLine.contains("hurtDesc:")){
						strLine = strLine.replace("hurtDesc:", "");
						userHurtdesc = strLine;
					} else if (strLine.contains("killDesc:")){
						strLine = strLine.replace("killDesc:", "");
						userKilldesc = strLine;	
						break;
					} else if (strLine.contains("accuracy:")){
						strLine = strLine.replace("accuracy:", "");
						userAccuracy = Integer.parseInt(strLine);
					}
				}
				in.close();
			} catch (Exception e){
				System.out.println("<ERROR> " + user + " was unable to access obj " + characterActual + " file");
				logging("<ERROR> " + user + " was unable to access obj " + characterActual + " file");
			}

			Random r = new Random();
			int gunDamage = r.nextInt(maxDamage-minDamage) + minDamage;
			int userGundamage = r.nextInt(userMaxdamage-userMindamage) + userMindamage;
			//TODO Generate number between 1 and 100 and see if it is within accuracy check...
			int accuracyCheck = r.nextInt(100);
			int userAccuracyCheck = r.nextInt(100);

			System.out.println(accuracyCheck + " accuracy:" + NPCaccuracy);
			logging(accuracyCheck + " accuracy:" + NPCaccuracy);

			if(accuracyCheck>NPCaccuracy){
				out.println(characterActual + " attempts to kill you but misses!");
			} else {
				out.println(characterActual + " attacks! " + hurtDesc + " [Damage:" +gunDamage+"]");
			}
			if(accuracyCheck>userAccuracy){
				out.println(user + " attempts to kill " + characterActual + " but misses!");
			} else {
				out.println(user + " attacks! " + userHurtdesc + " [Damage:" +userGundamage+"]");
			}



		}
		interpretUsr();
	}
	public void help(String input){
	}
	public void quit(){ //Exits the game and closes the thread
		out.println("We only part to meet again.");
		System.out.println("<Control> - " + user + " has left the game.");
		logging("<Control> - " + user + " has left the game.");
		//TODO send close to chatServer thread

		//Removes entry from the who file
		ArrayList<String> whoList = new ArrayList<String>();
		try{
			FileInputStream fstream = new FileInputStream(database + "/whp");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));	

			while ((strLine = br.readLine()) != null){
				whoList.add(strLine);
			}
			br.close();
		} catch (Exception e){
			System.out.println("<ERROR> " + user + " cannot access who file");
			logging("<ERROR> " + user + " cannot access access who file");
		}



		File file = new File(database + "/who");
		file.delete();
		try{
			FileWriter fstream = new FileWriter(database + "/who",true);
			BufferedWriter fileOut = new BufferedWriter(fstream);
			int i = whoList.size();

			while(i>-1){
				if (whoList.get(i)!=user){
					fileOut.write(whoList.get(i));
				}
				i--;
				break;
			}

			//Close the output stream
			fileOut.close();
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			logging("Error: " + e.getMessage());
		}

		try {
			socket.close();
		} catch(IOException e) {
			System.out.println("<ERROR> - Could not close connection with user");
			logging("<ERROR> - Could not close connection with user");
		}
	}
	public void inv(){//Prints the user's inventory
		String[] inventory;
		out.println("");
		out.println("--Inventory--");
		out.println("#   Name");
		try{
			FileInputStream fstream = new FileInputStream(database + "/charProfile/"+user+"/inventory");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));	

			while ((strLine = br.readLine()) != null){
				inventory = strLine.split(":");
				out.println(inventory[0] + " " + inventory[1]);
			}
			br.close();
		} catch (Exception e){
			System.out.println("<ERROR> " + user + " cannot access their inventory file");
			logging("<ERROR> " + user + " cannot access their inventory file");
		}
	}
	public void take(){//Takes an item from a room and puts it in the user's inventory
		//Allows a user to pick up an object from the ground
		//SYNTAX take $obj$ or take $obj$ ##




	}
	public void drop(String userInpt){//Allows a user to drop an item from their inventory to the room
		//Allows a user to drop an object from their inventory
		//SYNTAX drop $obj$ or drop $obj$ ## or drop $obj$ all

		//Delete inv file and then re-write without the dropped item

		//Read room inv file into array
		//Add num if inv item is already in room or create a new entry at end of array
		//Delete room inv file and re-write

		ArrayList<String> stringInv = new ArrayList<String>();
		ArrayList<Integer> numInv = new ArrayList<Integer>();
		ArrayList<String> stringRoom = new ArrayList<String>();
		ArrayList<String> numRoom = new ArrayList<String>();
		String[] inventory, userInputSplitter;
		int invNum = 0;
		int dropNum = 1; //The number of items to drop (-1 represents all items by that name in inv)
		String dropName = ""; //The name of the item to drop
		try{
			FileInputStream fstream = new FileInputStream(database + "/charProfile/"+user+"/inventory");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));	

			while ((strLine = br.readLine()) != null){
				inventory = strLine.split(":");
				numInv.add(Integer.parseInt(inventory[0]));
				stringInv.add(inventory[1]);
			}
			br.close();
		} catch (Exception e){
			System.out.println("<ERROR> " + user + " cannot access their inventory file");
			logging("<ERROR> " + user + " cannot access their inventory file");
			//A blank line in the users inv file can also cause this error
		}

		userInpt = userInpt.replace("drop ", "");
		userInputSplitter = userInpt.split(" ");
		if (userInputSplitter[0] == "all"){
			dropNum = -1;
		} else {
			try {
				dropNum = Integer.parseInt(userInputSplitter[0]);
			} catch (NumberFormatException e) {
				dropNum = 1;
			}
		}
		int i=0;
		try {
			while (true){
				System.out.println(dropName + ":" + stringInv.get(i));
				if (dropName.equals(stringInv.get(i))){
					break;
				}

				i++;
			}
		} catch (Exception e) {
			out.println("You can't drop an item that you don't have!");
			interpretUsr();
		}
		if (dropNum > 0){
			int tempInv = numInv.get(i);
			tempInv=tempInv-dropNum;
			numInv.set(i, tempInv);
		} else if (dropNum == -1){
			dropNum = numInv.get(i);
		} else {
			out.println("Sorry, I can't drop that many items!");
			interpretUsr();
		}

		File file = new File(database + "/charProfile/"+user+"/inventory");
		file.delete();
		try{
			// Create file 
			FileWriter fstream = new FileWriter(database + "/charProfile/"+user+"/inventory",true);
			BufferedWriter fileOut = new BufferedWriter(fstream);
			int invCounter = 0;
			while(true){
				if (numInv.get(invCounter) > 0){
					fileOut.write(numInv.get(invCounter) +":" + stringInv.get(invCounter));
					fileOut.newLine();
				}
				invCounter++;

				if (numInv.size() == invCounter){
					break;
				}
			}

			//Close the output stream
			fileOut.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			logging("Error: " + e.getMessage());
		}
		try {
			FileInputStream fstream2 = new FileInputStream(database + "/rooms/" + location + "/obj");
			DataInputStream in = new DataInputStream(fstream2);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			out.println(dropNum+":"+dropName);
			br.close();
		} catch (Exception e) {
		}
		interpretUsr();


	}
	public void logging(String logPhrase){//Logs errors to the users log file
		try{
			String fileloc = database + "/logs/"+user+".log";
			File file = new File(fileloc);
			//if file doesn't exists, then create it
			if(!file.exists()){
				file.createNewFile();
			}
			//true = append file
			BufferedWriter bW = new BufferedWriter(new FileWriter(database + "/logs/"+user+".log", true));
			bW.write(logPhrase);
			bW.newLine();
			bW.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void time(){// Will allow the retrieval of the system time in seconds.
	}
	public void commands(){
	}

	//---wizard functions---//
	public void wizardCheck(){
		//TODO add security check to make sure that user has wiz file &
	}
	public void wizardMenu(){//TODO make dialogue draw at 0,0 instead of halfway down
		setColor setColor = new setColor();
		out.println ((char)27 + "[2J");
		out.println ((char)27 + "[0m");
		out.println(setColor.color("#####################[red]Wizard[white]######################"));
		out.println(setColor.color("# Room ([blue]C[white])reation - Make new stuff              #"));
		out.println(setColor.color("# ([blue]O[white])nline Users  - See a list of online users  #"));
		out.println(setColor.color("# ([blue]C[white])hat Logs - - - Archive of conversations    #"));
		out.println(setColor.color("# ([blue]W[white])atch User  - - Eagle eye on a user         #"));
		out.println(setColor.color("# ([blue]M[white])ove  - - - - - Move a user to a room       #"));
		out.println(setColor.color("# ([blue]B[white])ump a user - - Bump a user off the server  #"));
		out.println(setColor.color("# ([blue]S[white])top  - - - - - Stop the server             #"));
		out.println(setColor.color("# ([blue]R[white])estart - - - - Restart the server          #"));
		out.println(setColor.color("# ([blue]L[white])ogout  - - - - Logout of Wizard            #"));
		out.println(setColor.color("# ([blue]P[white])rint - - - - - Reprint this dialogue       #"));
		out.println(setColor.color("#################################################"));
		UserInterface UI = new UserInterface(database, user);
		String str = UI.getInput(in);

		if (str.equals("c")){
			roomCreation();
		} else if (str.equals("p")){
			wizardMenu();
		} else {
			wizardMenu();
		}
	}
	public void roomCreation(){//TODO Not very practical...
		out.println ((char)27 + "[2J");
		out.println ((char)27 + "[0m");
		setColor setColor = new setColor();
		out.println(setColor.color("##################################[red]Room Creation[white]###################################"));
		out.println(setColor.color("# ([blue]R[white])oom Description (70 Characters/Line)                                        #"));
		out.println(setColor.color("#                                                                                #"));
		out.println(setColor.color("##################################################################################"));
		out.println(setColor.color("# ([blue]E[white])xits & Exit Descriptions                                                    #"));
		out.println(setColor.color("#  Direction  Name                            Room Description                   #"));
		out.println(setColor.color("#   [North] - none                                                               #"));
		out.println(setColor.color("#   [South] - none                                                               #"));
		out.println(setColor.color("#   [East]  - none                                                               #"));
		out.println(setColor.color("#   [West]  - none                                                               #"));
		out.println(setColor.color("#   [Up]    - none                                                               #"));
		out.println(setColor.color("#   [Down]  - none                                                               #"));
		out.println(setColor.color("##################################################################################"));
		out.println(setColor.color("# ([blue]N[white])on-Playable-Characters                                                      #"));
		out.println(setColor.color("#                                                                                #"));
		out.println(setColor.color("##################################################################################"));
		out.println(setColor.color("# ([blue]O[white])bjects                                                                      #"));
		out.println(setColor.color("#                                                                                #"));
		out.println(setColor.color("##################################################################################"));
		out.println(setColor.color("# ([blue]D[white])etailed Descriptions (Examine Command)                                      #"));
		out.println(setColor.color("#                                                                                #"));
		out.println(setColor.color("##################################################################################"));

		UserInterface UI = new UserInterface(database, user);
		String str = UI.getInput(in);

		if (str.equals("e")){

		}
	}
	public void onlineUsers(){//TODO

	}
	public void chatLogs(){//TODO

	}
	public void watchUser(){//TODO

	}
	public void moveUser(){//TODO

	}
	public void bumpUser(){//TODO

	}
	public void stopServer(){//TODO
	}
	public void restartServer(){//TODO
	}
}