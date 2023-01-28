package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String, User> userMap;
    private HashMap<Integer, Message> messageMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<String, User>();
        this.userMobile = new HashSet<>();
        this.messageMap = new HashMap<Integer, Message>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(userMobile.contains(mobile)) {
            throw new Exception("User already exists");
        }
        User user=new User(name, mobile);
        userMap.put(name, user);
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        String groupName="";
        if(users.size()>2){
            customGroupCount++;
            groupName="Group "+customGroupCount;
        }
        else if(users.size()==2){
            groupName=users.get(1).getName();
        }
        Group group=new Group(groupName, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        groupMessageMap.put(group, new ArrayList<Message>());
        return group;
    }

    public int createMessage(String content){
        messageId++;
        Message message=new Message(content, messageId);
        messageMap.put(messageId, message);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        List<User> users=groupUserMap.get(group);
        if(!users.contains(sender)){
            throw new Exception("You are not allowed to send message");
        }
        if(!groupMessageMap.containsKey(group)){
            groupMessageMap.put(group, new ArrayList<>());
        }
        groupMessageMap.get(group).add(message);
        senderMap.put(message, sender);
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(adminMap.containsKey(group) && !adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        List<User> users=groupUserMap.get(group);
        if(!users.contains(user)){
            throw new Exception("User is not a participant");
        }
        adminMap.put(group, user);
        return "SUCCESS";
    }
}
