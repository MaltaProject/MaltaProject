package maltaProject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
* This class is used to obtain stats about a user. Given the user's
* name and the location of the server database, a string with the specific
* stat will be returned.
*
* @author  Nicholas Ingalls
*/

public class UserValue {
	/**Stores database location - initialized by constructor*/
	String database = "";
	/**Stores username - initialized by constructor*/
	String user = "";
	
	/**
	* Constructs a new object with a string containing the database location and a string containing the username.
	*/ 
	public UserValue(String data, String username){
		database = data;
		user = username;
	}
	/**
	 * Used to obtain the age of a user.
	 * @return A String containing the age of a user
	 */
	public String getAge(){
		String fileLoc = database + "/charProfile/"+user+"/age";
		FileOperations open = new FileOperations(fileLoc);
		String userAge = open.getLine();
		return userAge;
	}
	/**
	 * Used to obtain the class of a user.
	 * @return A String containing the class of a user
	 */
	public String getUserClass(){
		String fileLoc = database + "/charProfile/"+user+"/class";
		FileOperations open = new FileOperations(fileLoc);
		String userClass = open.getLine();
		return userClass;
	}
	/**
	 * Used to obtain the dexterity of a user.
	 * @return A String containing the dexterity of a user
	 */
	public String getDex(){
		String fileLoc = database + "/charProfile/"+user+"/dexterity";
		FileOperations open = new FileOperations(fileLoc);
		String userDex = open.getLine();
		return userDex;
	}
	/**
	 * used to obtain the experience of a user.
	 * @return A String containing the experience of a user
	 */
	public String getXP(){
		String fileLoc = database + "/charProfile/"+user+"/experience";
		FileOperations open = new FileOperations(fileLoc);
		String userXP = open.getLine();
		return userXP;
	}
	/**
	 * used to return the amount of gold that a user has.
	 * @return A String containing the amount of gold
	 */
	public String getGold(){
		String fileLoc = database + "/charProfile/"+user+"/gold";
		FileOperations open = new FileOperations(fileLoc);
		String userGold = open.getLine();
		return userGold;
	}
	/**
	 * Used to obtain the health of a user. This is the current health, as opposted to @see getMaxHealth
	 * @return A String containing the health of a user
	 */
	public String getHP(){
		String fileLoc = database + "/charProfile/"+user+"/health";
		FileOperations open = new FileOperations(fileLoc);
		String userHP = open.getLine();
		return userHP;
	}
	/**
	 * Used to obtain the intelligence of a user.
	 * @return A String containing the intelligence of a user
	 */
	public String getInt(){
		String fileLoc = database + "/charProfile/"+user+"/intelligence";
		FileOperations open = new FileOperations(fileLoc);
		String userInt = open.getLine();
		return userInt;
	}
	/**
	 * Used to obtain the level of a user.
	 * @return A String containing the level of a user
	 */
	public String getLvl(){
		String fileLoc = database + "/charProfile/"+user+"/level";
		FileOperations open = new FileOperations(fileLoc);
		String userLvl = open.getLine();
		return userLvl;
	}
	/**
	 * Used to obtain the location of a user.
	 * @return A String containing the location (Name of the room) of a user
	 */
	public String getLoc(){
		String fileLoc = database + "/charProfile/"+user+"/location";
		FileOperations open = new FileOperations(fileLoc);
		String userLoc = open.getLine();
		return userLoc;
	}
	/**
	 * Used to obtain the race of a user.
	 * @return A String containing the race of a user
	 */
	public String getRace(){
		String fileLoc = database + "/charProfile/"+user+"/race";
		FileOperations open = new FileOperations(fileLoc);
		String userRace = open.getLine();
		return userRace;
	}
	/**
	 * Used to obtain the religion of a user.
	 * @return A String containing the age of a user
	 */
	public String getReligion(){
		String fileLoc = database + "/charProfile/"+user+"/religion";
		FileOperations open = new FileOperations(fileLoc);
		String userReligion = open.getLine();
		return userReligion;
	}
	/**
	 * Used to obtain the gender of a user.
	 * @return A String containing the gender of a user
	 */
	public String getSex(){
		String fileLoc = database + "/charProfile/"+user+"/sex";
		FileOperations open = new FileOperations(fileLoc);
		String userSex = open.getLine();
		return userSex;
	}
	/**
	 * Used to obtain the strength of a user.
	 * @return A String containing the strength of a user
	 */
	public String getStr(){
		String fileLoc = database + "/charProfile/"+user+"/strength";
		FileOperations open = new FileOperations(fileLoc);
		String userStr = open.getLine();
		return userStr;
	}
	/**
	 * Used to obtain the weight of a user.
	 * @return A String containing the weight of a user
	 */
	public String getWeight(){
		String fileLoc = database + "/charProfile/"+user+"/weight";
		FileOperations open = new FileOperations(fileLoc);
		String userWeight = open.getLine();
		return userWeight;
	}
	/**
	 * Used to obtain the maximum health of a user.
	 * @return A String containing the maximum health of a user
	 */
	public String getMaxHealth(){
		String fileLoc = database + "/charProfile/"+user+"/maxhealth";
		FileOperations open = new FileOperations(fileLoc);
		String maxHealth = open.getLine();
		return maxHealth;
	}
	/**
	 * Used to obtain the wisdom of a user.
	 * @return A String containing the wisdom of a user
	 */
	public String getWis(){
		String fileLoc = database + "/charProfile/"+user+"/wisdom";
		FileOperations open = new FileOperations(fileLoc);
		String userWis = open.getLine();
		return userWis;
	}
	
}
