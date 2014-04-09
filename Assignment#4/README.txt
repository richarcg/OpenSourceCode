Ian Mundy
Caleb Richard
Tic Tac Toe Pub Sub server

Our server is located at ip 54.186.216.144 and accepts traffic on port 20000

It is adapted from the udpgroupchat server

JOIN
Joins the user to a game group (groups may only contain 2 users)
If no group containing only one player exists, a new one is made
The client is sent back a message with the GAME Game# Player#
with 0 being the first player to join and 1 being the second.
This message does not have to be ACKed
Once the second player joins the first player is sent a message with TURN
that they must POLL for and ACK

POLL
Returns the messages for the client that polls
Note that the clients messages are stored in their group object
Will resend the message in 10 seconds if not ACKed
*At time of writing server needs updated. POLL is in a while loop
	will run continuously until message is acked(without delay)
	however, this does not affect the gameplay.

MSG
MSG GAME# CONTENT
Sends a message to the users in a particular group.
Messages are delivered upon polling.
Moves are sent with this. The expected format is:
	MSG GAME# MOVE LOCATION
where location is 0-8

ACK
ACK GAME# CONTENT
Should return the content and game# to server
ACKed messages will be removed from the message queue in that group.

END
END GAME#
Ends the specified game
Note that this means a user will not be able to poll that group
for messages even if they had any left as the group will not exist
*At the time of writing the server needs a small update. There is no
	return after the END case, and will also return BAD REQUEST
	to the sender. However this does not affect gameplay as the
	user should not be listening after it sends END.
*