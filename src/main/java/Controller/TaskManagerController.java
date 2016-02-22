package Controller;



import Model.*;
import view.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.File;
import java.time.Duration;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Created by васыль on 02.02.2016.
 */

public class TaskManagerController {
    final static Logger logger = Logger.getLogger(TaskManagerController.class);

    static TaskManagerJFrame mainWindow = new TaskManagerJFrame();
    public static TaskList taskList = new ArrayTaskList();
    public static SortedMap<Date, Set<Task>> onWeek = new TreeMap();
    private static Task task;
    public static DefaultListModel<Task> model;
    public static DefaultListModel<Map.Entry<Date, Set<Task>>> calendarModel;


    public static void main(String[] args) {


        NotfyController notfy = new NotfyController();
        TaskIO.readBinary(taskList, new File("filetasks"));
        notfy.start();
        logger.warn("taskManager Started");
        modelCreater();


        logger.warn("notify system started");

        mainWindow.paintPanel(new MainPanel());


    }

    public class TaskListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            TaskManagerController.task = (Task) AllTaskListPanel.allTasksList.getSelectedValue();
            mainWindow.paintPanel(new TaskPanel(task));

        }
    }

    public class TaskActiveListener implements ItemListener {
        boolean onOff;
        int selected;

        @Override
        public void itemStateChanged(ItemEvent e) {
            selected = e.getStateChange();
            if (selected == 1)
                onOff = true;
            else
                onOff = false;


            activeChanger(onOff);
        }
    }

    public class CreateTaskListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            mainWindow.paintPanel(new CreateTaskPanel());


        }

    }

    public class ChangeTaskButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            CreateTaskPanel changePanel = new CreateTaskPanel();
            mainWindow.paintPanel(changePanel);
            CreateTaskPanel.primaryParameters(task);
            CreateTaskPanel.initializeParemeters();


        }
    }

    public class CreateButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            CreateTaskPanel.initializeParemeters();
            if (CreateTaskPanel.time != null)

                taskList.add(new Task(CreateTaskPanel.title, CreateTaskPanel.time));
            else
                taskList.add(new Task(CreateTaskPanel.title, CreateTaskPanel.start, CreateTaskPanel.end, CreateTaskPanel.interval));
            mainWindow.paintPanel(new AllTaskListPanel(model));
            TitleChanger(CreateTaskPanel.title);
            TimeChanger(CreateTaskPanel.time);
            startChanger(CreateTaskPanel.start);
            endChanger(CreateTaskPanel.end);
            IntervalChanger(CreateTaskPanel.interval);


        }

    }

    public class RemoveTaskButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            taskList.remove(task);
            modelCreater();
            mainWindow.paintPanel(new AllTaskListPanel(model));
        }
    }

    public class SeeTaskListListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            modelCreater();
            mainWindow.paintPanel(new AllTaskListPanel(model));


        }

    }

    public class CalendarListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            modelCalendarCreater();
            mainWindow.paintPanel(new AllTaskListPanel(calendarModel));
        }
    }

    public class TaskListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mainWindow.paintPanel(new TaskPanel());
        }
    }

    public class BackButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mainWindow.paintPanel(new MainPanel());
        }

    }


    private int getTaskChangedIndex() {
        int indexChangedTask = 0;
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.getTask(i).equals(task)) {
                indexChangedTask = i;
                break;
            }
        }
        return indexChangedTask;


    }

    public class CloseAll implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            Date current = new Date(System.currentTimeMillis());
            for (Task task : taskList) {
                if (task.getEnd().before(current) && task.getTime().before(current))
                    taskList.remove(task);
            }
            File file = new File("filetasks");
            file.delete();
            TaskIO.writeBinary(taskList, file);
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    }

    private void TitleChanger(String title) {
        taskList.getTask(this.getTaskChangedIndex()).setTitle(title);
    }

    private void TimeChanger(Date time) {
        taskList.getTask(this.getTaskChangedIndex()).setTime(time);
    }

    private void startChanger(Date start) {
        taskList.getTask(this.getTaskChangedIndex()).setStart(start);
    }

    private void endChanger(Date end) {
        taskList.getTask(this.getTaskChangedIndex()).setEnd(end);
    }

    private void IntervalChanger(Duration interval) {
        taskList.getTask(this.getTaskChangedIndex()).setInterval(interval);
    }

    private void activeChanger(boolean active) {
        taskList.getTask(this.getTaskChangedIndex()).setActive(active);
    }

    private static void modelCreater() {
        model = new DefaultListModel();

        for (int i = 0; i < taskList.size(); i++) {

            model.add(i, taskList.getTask(i));

        }
    }

    private void modelCalendarCreater() {
        calendarModel = new DefaultListModel<>();
        Date from = new Date(System.currentTimeMillis());
        Date to = new Date(System.currentTimeMillis() + 86400000 * 7);
        onWeek = Tasks.calendar(taskList, from, to);
        for (Map.Entry entry : onWeek.entrySet()) {
            calendarModel.addElement(entry);
        }
    }
}




