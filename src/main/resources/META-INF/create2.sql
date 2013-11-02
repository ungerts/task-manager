-- -----------------------------------------------------
-- Table HumanTaskModel
-- -----------------------------------------------------
CREATE TABLE HUMANTASKMODEL (
  ID INTEGER NOT NULL, ACTIVATIONTIME LONGVARBINARY,
  COMPLETEBY LONGVARBINARY,
  DURATIONAVG LONGVARBINARY,
  DURATIONMAX LONGVARBINARY,
  DURATIONMIN LONGVARBINARY,
  FAULTSCHEMA LONGVARBINARY,
  INPUTSCHEMA LONGVARBINARY,
  NAME VARCHAR,
  OUTPUTSCHEMA LONGVARBINARY,
  POSITIONX LONGVARBINARY,
  POSITIONY LONGVARBINARY,
  PRIORITY LONGVARBINARY,
  QUERYPROPERTY1 LONGVARBINARY,
  QUERYPROPERTY1NAME VARCHAR,
  QUERYPROPERTY2 LONGVARBINARY,
  QUERYPROPERTY2NAME VARCHAR,
  QUERYPROPERTY3 LONGVARBINARY,
  QUERYPROPERTY3NAME VARCHAR,
  QUERYPROPERTY4 LONGVARBINARY,
  QUERYPROPERTY4NAME VARCHAR,
  SKIPABLE LONGVARBINARY,
  STARTBY LONGVARBINARY,
  PRIMARY KEY (ID),
  CONSTRAINT unique_HumanTaskModel_Name UNIQUE (name));


-- -----------------------------------------------------
-- Table "LogicalPeopleGroupDef"
-- -----------------------------------------------------

CREATE TABLE LOGICALPEOPLEGROUPDEF (
  ID INTEGER NOT NULL,
  NAME VARCHAR,
  PRIMARY KEY (ID),
  CONSTRAINT unique_LogicalPeopleGroupDef_Name UNIQUE (name));


-- -----------------------------------------------------
-- Table LPG_ArgumentDef
-- -----------------------------------------------------

CREATE TABLE LPG_ARGUMENTDEF (
  ID INTEGER NOT NULL,
  NAME VARCHAR,
  LPG INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_LPG
  FOREIGN KEY (lpg )
  REFERENCES LogicalPeopleGroupDef (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
  CONSTRAINT unique_LPG_ArgumentDef_Name UNIQUE (name, lpg));

-- -----------------------------------------------------
-- Table "Literal"
-- -----------------------------------------------------

CREATE TABLE LITERAL (ID INTEGER NOT NULL,
  ENTITYIDENTIFIER VARCHAR,
  HUMANROLE VARCHAR,
  HUMANTASKMODEL_ID INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_Literal_HumanTaskModel
  FOREIGN KEY (HumanTaskModel_id)
  REFERENCES HumanTaskModel (id)
  ON DELETE CASCADE
  ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table PeopleQuery
-- -----------------------------------------------------

CREATE TABLE PEOPLEQUERY (
  ID INTEGER NOT NULL,
  HUMANROLE VARCHAR,
  HUMANTASKMODEL_ID INTEGER,
  LPG INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_PeopleQuery
  FOREIGN KEY (lpg )
  REFERENCES LogicalPeopleGroupDef (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  CONSTRAINT fk_PeopleQuery_HumanTaskModel
  FOREIGN KEY (HumanTaskModel_id)
  REFERENCES HumanTaskModel (id)
  ON DELETE CASCADE
  ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table PeopleQueryArgument
-- -----------------------------------------------------

CREATE TABLE PEOPLEQUERYARGUMENT (ID INTEGER NOT NULL,
  EXPRESSION LONGVARBINARY,
  LPGARGUMENT INTEGER, PEOPLEQUERY INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_PeopleQuery_ID
  FOREIGN KEY (peopleQuery )
  REFERENCES PeopleQuery (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
  CONSTRAINT fk_LPG_ArgumentDef
  FOREIGN KEY (lpgArgument )
  REFERENCES LPG_ArgumentDef (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table PresentationInformation
-- -----------------------------------------------------

CREATE TABLE PRESENTATIONINFORMATION (
  ID INTEGER NOT NULL,
  DESCRIPTION LONGVARBINARY,
  SUBJECT VARCHAR, TITLE VARCHAR,
  HUMANTASKMODEL_ID INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_PresentationInfo_HumanTaskModel
  FOREIGN KEY (HumanTaskModel_id )
  REFERENCES HumanTaskModel (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION);




-- -----------------------------------------------------
-- Table HumanTaskInstance
-- -----------------------------------------------------

CREATE TABLE HUMANTASKINSTANCE (
  ID INTEGER NOT NULL,
  ACTIVATIONTIME TIMESTAMP,
  COMPLETEBY TIMESTAMP,
  CONTEXTID VARCHAR,
  CREATEDON TIMESTAMP,
  DURATIONAVG BIGINT,
  DURATIONMAX BIGINT,
  DURATIONMIN BIGINT,
  EXPIRATIONTIME TIMESTAMP,
  FAULTDATA LONGVARBINARY,
  FAULTNAME VARCHAR,
  INPUTDATA LONGVARBINARY,
  NAME VARCHAR,
  OUTPUTDATA LONGVARBINARY,
  POSITIONX DOUBLE,
  POSITIONY DOUBLE,
  PRESENTATIONDESCRIPTION LONGVARBINARY,
  PRESENTATIONNAME VARCHAR,
  PRESENTATIONSUBJECT VARCHAR,
  PRIORITY INTEGER,
  QUERYPROPERTY1 VARCHAR,
  QUERYPROPERTY1NAME VARCHAR,
  QUERYPROPERTY2 VARCHAR,
  QUERYPROPERTY2NAME VARCHAR,
  QUERYPROPERTY3 DOUBLE,
  QUERYPROPERTY3NAME VARCHAR,
  QUERYPROPERTY4 DOUBLE,
  QUERYPROPERTY4NAME VARCHAR,
  SKIPABLE SMALLINT,
  STARTBY TIMESTAMP,
  STATUS VARCHAR,
  SUSPENDUNTIL TIMESTAMP,
  TASKPARENTID VARCHAR,
  MODELID INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_modelId
  FOREIGN KEY (modelId )
  REFERENCES HumanTaskModel (id )
  ON DELETE SET NULL
  ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table AssignedUser
-- -----------------------------------------------------

CREATE TABLE ASSIGNEDUSER (
  ID INTEGER NOT NULL,
  USERID VARCHAR UNIQUE,
  PRIMARY KEY (ID),
  CONSTRAINT unique_AssignedUserId UNIQUE (userid));


-- -----------------------------------------------------
-- Table Attachment
-- -----------------------------------------------------

CREATE TABLE ATTACHMENT (
  ID INTEGER NOT NULL,
  ACCESSTYPE VARCHAR,
  ATTACHEDAT TIMESTAMP,
  CONTENTTYPE VARCHAR,
  NAME VARCHAR,
  VALUE LONGVARBINARY,
  TIID INTEGER,
  ATTACHEDBY INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_Attachment_HumanTaskInstance
  FOREIGN KEY (tiid)
  REFERENCES HumanTaskInstance (id)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
  CONSTRAINT fk_Attachment_Creator
  FOREIGN KEY (attachedBy)
  REFERENCES AssignedUser (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  CONSTRAINT unique_Attachment_Name UNIQUE (name, tiid));



-- -----------------------------------------------------
-- Table CallbackCorrelationProperty
-- -----------------------------------------------------

CREATE TABLE CALLBACKCORRELATIONPROPERTY (
  ID INTEGER NOT NULL,
  NAME VARCHAR,
  VALUE LONGVARBINARY,
  TIID INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_CallbackCorrelation_HumanTaskInstance
  FOREIGN KEY (tiid )
  REFERENCES HumanTaskInstance (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table WorkItem
-- -----------------------------------------------------

CREATE TABLE WORKITEM (
  ID INTEGER NOT NULL,
  CLAIMED SMALLINT,
  CREATIONTIME TIMESTAMP,
  EVERYBODY SMALLINT,
  GENERICHUMANROLE VARCHAR,
  TIID INTEGER,
  ASSIGNEE INTEGER,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AssignedUser
  FOREIGN KEY (assignee )
  REFERENCES AssignedUser (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
  CONSTRAINT fk_AssignedTask
  FOREIGN KEY (tiid )
  REFERENCES HumanTaskInstance (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table Users
-- -----------------------------------------------------

CREATE TABLE USERS (
  ID INTEGER NOT NULL,
  FIRSTNAME VARCHAR,
  LASTNAME VARCHAR,
  USERID VARCHAR,
  PRIMARY KEY (ID),
  CONSTRAINT unique_User_userid UNIQUE (userid));

-- -----------------------------------------------------
-- Table Groups
-- -----------------------------------------------------

CREATE TABLE GROUPS (
  ID INTEGER NOT NULL,
  GROUPNAME VARCHAR,
  PRIMARY KEY (ID),
  CONSTRAINT unique_Group_groupname UNIQUE (groupName));

-- -----------------------------------------------------
-- Table Users_has_Groups
-- -----------------------------------------------------

CREATE TABLE USERSGROUPS (
  GROUP_ID INTEGER NOT NULL,
  USER_ID INTEGER NOT NULL,
  PRIMARY KEY (GROUP_ID, USER_ID),
  CONSTRAINT fk_User_has_Group
  FOREIGN KEY (User_id)
  REFERENCES Users(id)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
  CONSTRAINT fk_Group_has_User
  FOREIGN KEY (Group_id)
  REFERENCES Groups (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- TABLE SEQUENCE : Helper table required for JPA to manage keys
-- -----------------------------------------------------
CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50), SEQ_COUNT DECIMAL(15));
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0);


-- -----------------------------------------------------
-- VIEW WorkItemTaskView
-- -----------------------------------------------------

create view WorkItemTaskView(
	tiid, wiid, createdOn, assignee, assignedToEverybody, genericHumanRole, isClaimed,
	taskInstanceName, taskModelId, status, priority, isSkipable, taskCreatedOn, activatedOn, expiresOn, startBy, completeBy,
	suspendUntil, presentationName, presentationSubject, presentationDescription,
	inputData, outputData, faultName, faultData)
as select ti.id, wi.id, wi.creationTime, au.userId, wi.everybody, wi.genericHumanRole, wi.claimed, ti.name, ti.modelId, ti.status, ti.priority, ti.skipable, ti.createdOn, ti.activationTime, ti.expirationTime, ti.startBy,ti.completeBy,
ti.suspendUntil, ti.presentationName, ti.presentationSubject, ti.presentationDescription, ti.inputData, ti.outputData, ti.faultName, ti.faultData
from HumanTaskInstance ti, WorkItem wi, Assigneduser au where ti.id = wi.tiid and au.id = wi.assignee;

-- -----------------------------------------------------
-- TABLE STRUCTUREDATA
-- -----------------------------------------------------

CREATE TABLE STRUCTUREDATA (
  ID INTEGER NOT NULL,
  HASCONTROLLEDTASKS BOOLEAN,
  HASSUBTASKS BOOLEAN,
  LOCKCOUNTER INTEGER,
  NAME VARCHAR,
  STRUCTURENR INTEGER,
  STRUCTURE_ID INTEGER,
  SUSPENDCOUNTER INTEGER,
  TASK_ID INTEGER,
  MERGETASK_ID INTEGER,
  PARENTTASK_ID INTEGER,
  PRIMARY KEY (ID));
ALTER TABLE STRUCTUREDATA ADD CONSTRAINT FK_STRUCTUREDATA_PARENTTASK_ID FOREIGN KEY (PARENTTASK_ID) REFERENCES STRUCTUREDATA (ID);
ALTER TABLE STRUCTUREDATA ADD CONSTRAINT FK_STRUCTUREDATA_MERGETASK_ID FOREIGN KEY (MERGETASK_ID) REFERENCES STRUCTUREDATA (ID);