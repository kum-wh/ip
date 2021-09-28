package duke.Parser;

import duke.Command.*;
import duke.ErrorHandling.CommandException;
import duke.ErrorHandling.ErrorStaticString;
import duke.TaskList.TaskList;

import java.util.Objects;

public class Parser{

    private static final String COMMAND_VIEW_LIST = "list";
    private static final String COMMAND_ECHO = "echo";
    private static final String COMMAND_COMPLETE_TASK = "done";
    private static final String COMMAND_ADD_TODO = "todo";
    private static final String COMMAND_ADD_EVENT = "event";
    private static final String COMMAND_ADD_DEADLINE = "deadline";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_EXIT = "bye";
    private static final String COMMAND_DATE_TASK = "date";
    private static final String COMMAND_FIND_WORD = "find";
    private static final String EMPTY_STRING = "";
    private static final String COMMAND_GUIDE_INDICATOR = "!";
    private static final String SPLITTER = ";";

    private String userInput;
    private final TaskList listManager;

    public Parser(String userInput, TaskList listManager){
        this.userInput = userInput;
        this.listManager = listManager;
    }

    private String taskCategory(String userInput) {
        if (userInput.startsWith(COMMAND_VIEW_LIST)) {
            return COMMAND_VIEW_LIST;
        }
        if (userInput.startsWith(COMMAND_ADD_DEADLINE)) {
            return COMMAND_ADD_DEADLINE;
        }
        if (userInput.startsWith(COMMAND_ADD_TODO)) {
            return COMMAND_ADD_TODO;
        }
        if (userInput.startsWith(COMMAND_ADD_EVENT)) {
            return COMMAND_ADD_EVENT;
        }
        if (userInput.startsWith(COMMAND_COMPLETE_TASK)) {
            return COMMAND_COMPLETE_TASK;
        }
        if (userInput.startsWith(COMMAND_ECHO)) {
            return COMMAND_ECHO;
        }
        if (userInput.startsWith(COMMAND_DELETE)) {
            return COMMAND_DELETE;
        }
        if (userInput.startsWith(COMMAND_DATE_TASK)) {
            return COMMAND_DATE_TASK;
        }
        if(userInput.startsWith(COMMAND_FIND_WORD)){
            return COMMAND_FIND_WORD;
        }
        return null;
    }

    private boolean isTodo(){
        if(userInput.contains(COMMAND_ADD_TODO)){
            userInput = userInput.replaceAll(COMMAND_ADD_TODO,SPLITTER + COMMAND_ADD_TODO);
            return true;
        }
        return false;
    }
    private boolean isEvent(){
        if(userInput.contains(COMMAND_ADD_EVENT)){
            userInput = userInput.replaceAll(COMMAND_ADD_EVENT,SPLITTER + COMMAND_ADD_EVENT);
            return true;
        }
        return false;
    }
    private boolean isDeadline(){
        if(userInput.contains(COMMAND_ADD_DEADLINE)){
            userInput = userInput.replaceAll(COMMAND_ADD_DEADLINE,SPLITTER + COMMAND_ADD_DEADLINE);
            return true;
        }
        return false;
    }

    private boolean isList(){
        return userInput.startsWith(COMMAND_VIEW_LIST);
    }

    public boolean isExit(){
        return userInput.startsWith(COMMAND_EXIT);
    }

    public void handleInput() throws CommandException{
        if(userInput.isEmpty()){
            throw new CommandException(ErrorStaticString.ERROR_NULL);
        }
        if(userInput.startsWith(COMMAND_GUIDE_INDICATOR)){
            CommandGuide commandGuide = new CommandGuide(userInput);
            commandGuide.executeCommand();
        }else{
            splitInputIfThereMoreThanOneCommand();
        }
    }

    private void splitInputIfThereMoreThanOneCommand() {
        userInput = userInput.replaceAll(SPLITTER,EMPTY_STRING);
        boolean isAddingTask = false;
        if (!isList()) {
            Boolean isTodo = isTodo();
            Boolean isEvent = isEvent();
            Boolean isDeadline = isDeadline();
            isAddingTask = isTodo || isEvent || isDeadline;
        }
        if(isAddingTask) {
            String[] commandList = userInput.split(SPLITTER);
            for (int i = 1; i < commandList.length; i++) {
                handleCommand(commandList[i]);
            }
        }else{
            handleCommand(userInput);
        }
    }

    private void handleCommand(String inputCommand){
        String commandCategory = taskCategory(inputCommand);
        try {
            switch (Objects.requireNonNull(commandCategory)) {
            case COMMAND_VIEW_LIST:
                ListCommand listCommand = new ListCommand(inputCommand, listManager);
                try {
                    listCommand.executeCommand();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_COMPLETE_TASK:
                DoneCommand doneCommand = new DoneCommand(inputCommand, listManager);
                try {
                    doneCommand.executeCommand();
                    listManager.printList();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_ADD_EVENT:
                AddEventCommand addEvent = new AddEventCommand(inputCommand, listManager);
                try {
                    addEvent.executeCommand();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_ADD_TODO:
                AddToDoCommand addToDo = new AddToDoCommand(inputCommand, listManager);
                try {
                    addToDo.executeCommand();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_ADD_DEADLINE:
                AddDeadlineCommand addDeadline = new AddDeadlineCommand(inputCommand, listManager);
                try {
                    addDeadline.executeCommand();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_ECHO:
                EchoCommand echoCommand = new EchoCommand(inputCommand);
                try {
                    echoCommand.executeCommand();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_DELETE:
                DeleteCommand deleteCommand = new DeleteCommand(inputCommand, listManager);
                try {
                    deleteCommand.executeCommand();
                } catch (CommandException e) {
                    e.handleException();
                }
                break;
            case COMMAND_DATE_TASK:
                FindDateCommand findDateCommand = new FindDateCommand(inputCommand, listManager);
                try {
                    findDateCommand.executeCommand();
                }catch (CommandException e){
                    e.handleException();
                }
                break;
            case COMMAND_FIND_WORD:
                FindKeywordCommand findKeywordCommand = new FindKeywordCommand(inputCommand, listManager);
                try {
                    findKeywordCommand.executeCommand();
                }catch (CommandException e){
                    e.handleException();
                }
            }
        }catch(NullPointerException e){
            System.out.println(ErrorStaticString.ERROR_UNKNOWN_COMMAND);
            EchoCommand echoCommand = new EchoCommand(inputCommand);
            try {
                echoCommand.executeCommand();
            } catch (CommandException echoE) {
                echoE.handleException();
            }
        }
    }
}
