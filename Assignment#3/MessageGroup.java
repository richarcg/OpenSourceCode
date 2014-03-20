//Author: Caleb Richard
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//This class is used as a Message Group for the MessageServer.
//If this code is revisited, additional functionality such as
//ability to change the maxSize of the group should be implemented
public class MessageGroup {
	
	protected final String groupName;
	protected final static Map<Integer, Vector<String>> members = (new HashMap<Integer, Vector<String>>());
	protected int maxSize;

	/**
	 * Constructor for MessageGroup
	 * @param name - name of the group
	 * @param max - max size of group, -1 means
	 * 				no max size
	 */
	public MessageGroup(String name, int max) {
		this.groupName = name;
		this.maxSize = max;
	}
	
	/**
	 * Returns if a member with certain id is
	 * in group or not.
	 * @param id - id of member to be checked
	 * @return - if member id is in group
	 */
	public Boolean inGroup (int id) {
		return members.containsKey(id);
	}
	
	/**
	 * Adds a member with id to group
	 * @param id - id of member to be added
	 * @return - true if member successfully added
	 * or if member was already in group, false if group 
	 * was at maxSize already
	 */
	public Boolean addMember (int id) {
		boolean success = false;
		if (inGroup(id))
		{
			success = true;
		} else if (members.size() < maxSize || maxSize == -1) {
			members.put(id, new Vector<String>());
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Removes member from group
	 * @param id - id of member to be deleted
	 */
	public void deleteMember (int id) {
		members.remove(id);
	}
	
	/**
	 * Adds message to everyone except the posters list
	 * of messages
	 * @param owner - poster of message
	 * @param message - message to be posted
	 */
	public void addMessage (int owner, String message) {
		System.out.println(owner);
		ClientEndPoint client = MessageServer.clientEndPoints.get(owner);
		String name = client.getName();
		for ( Map.Entry<Integer, Vector<String>> entry: members.entrySet())
		{
			System.out.println("almmostadded: " +message);
			if (entry.getKey() != client.getID())
			{
				entry.getValue().add(this.groupName + " ; " + name + " : " + message);
				System.out.println("added: " +message);
			}
		}
	}
	
	/**
	 * Deletes message from member's list of
	 * messages
	 * @param member - member to delete message from
	 * @param message - message to delete
	 */
	public void deleteMessage(int member, String message) {
		members.get(member).remove(message);
	}
	
	/**
	 * Returns first message in list of 
	 * member's message list
	 * @param id - member to get message for
	 * @return - message for member
	 */
	public String getMessage (int id) {
		String message = null;
		if (!members.get(id).isEmpty())
		{
			message = members.get(id).firstElement();
			System.out.println("retrieved: "+message);
		}
		return message;
	}
}
