# clientuserscript.sql
# The client user execution script for Project Two - CNT 4714 - Summer 2022
# all commands assumed to be executed by the client user
# the client user has only selection  privilege on the project2 database schema
# Any command that is not a 'SELECT' command will be denied.
#	7 out of 10 commands will succeed in this script.
#	7 out of 10 commands are 'SELECT' commands.

# Command 1:
#   Query: Which rider won the World Championship - Elite Women in 2021?
SELECT ridername 
FROM racewinners 
WHERE racename = 'World Championship - Elite Women' AND raceyear = 2021;

# Commands 2A, 2B, and 2C:
#   Delete all the riders from Norway from the riders table.
#   * * * Do a "before" and "after" select * from riders for this command.
#   Note: the before and after select statements will execute, but the delete will not
#         thus no changes will be reflected in the before and after snapshots.
#		why: user 'client' does not have 'DELETE' privileges

# 2A:
SELECT * FROM riders;

# 2B:
DELETE FROM riders WHERE nationality = 'Norway';

# 2C:
SELECT * FROM riders;

# Commands 3A, 3B, and 3C:
#    Update rider Marianne Vos to show number of wins = 245 in the riders table.
# * * Do a "before" and "after" selection on the riders table
#   Note: the before and after select statements will execute, but the delete will not
#         thus no changes will be reflected in the before and after snapshots.
#		why: user 'client' does not have 'UPDATE' privileges

# 3A:
SELECT * FROM riders;

# 3B:
UPDATE riders SET num_pro_wins = 245 WHERE ridername = "Marianne Vos";

# 3C:
SELECT * FROM riders;

# Command 4:
#   Query: Which rider won the 2021 Tour de France?
SELECT ridername
FROM racewinners
WHERE racename = "Tour de France" AND raceyear = 2021;

# Command 5:
#   How many riders are there?
SELECT count(ridername) as number_of_riders
FROM riders;

# Command 6:
#   updating command not valid for the client user
UPDATE teams
SET registered_nation = "France"
WHERE teamname = "Movistar";
