
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Tin
 */
public class SwapTest {

    private static Segment head;
    private static LinkedList<Job> jobList;
    private static LinkedList<Job> addList;
    private static Segment current;

    public static void main(String[] args) {
        head = new Segment(0, 0, 100, null);
        jobList = new LinkedList();
        addList = new LinkedList();
        String input = "";
        Scanner kb = new Scanner(System.in);
        while (!input.equalsIgnoreCase("exit")) {
            System.out.print(">");
            input = kb.nextLine();
            String command[] = input.split(" ");
            if (command[0].equalsIgnoreCase("add")) {
                addJob(command[1]);
            } else if (command[0].equalsIgnoreCase("jobs")) {
                showJobs();
            } else if (command[0].equalsIgnoreCase("list")) {
                showMemoryList();
            } else if (command[0].equalsIgnoreCase("ff")) {
                boolean firstfit = firstFit(command[1]);
                if (firstfit == false) {
                    System.err.println("Cannnot allocate process in memory!");
                }

            } else if (command[0].equalsIgnoreCase("nf")) {
                boolean nextfit = nextFit(command[1]);
                if (nextfit == false) {
                    System.err.println("Cannnot allocate process in memory!");
                }

            } else if (command[0].equalsIgnoreCase("bf")) {
                boolean bestfit = bestFit(command[1]);
                if (bestfit == false) {
                    System.err.println("Cannnot allocate process in memory!");
                }

            } else if (command[0].equalsIgnoreCase("wf")) {
                boolean worstfit = worstFit(command[1]);
                if (worstfit == false) {
                    System.err.println("Cannnot allocate process in memory!");
                }

            } else if (command[0].equalsIgnoreCase("de")) {
                boolean removed = deAllocate(command[1]);
                if (removed == false) {
                    System.err.println("Cannnot de-allocate process in memory!");
                }

            } else if (!command[0].equalsIgnoreCase("exit")) {
                System.err.println("Command does not exist!");
            }
        }
    }

    public static void addJob(String idAndSize) {
        int pid = 0;
        int size = 0;
        try {
            String[] vals = idAndSize.split(",");
            pid = Integer.parseInt(vals[0]);
            size = Integer.parseInt(vals[1]);
        } catch (NumberFormatException e) {
            System.err.println("Wrong input format");
            return;
        }
        boolean canAdd = true;
        for (Job jobs : jobList) {
            if (pid == jobs.getPid()) {
                System.err.println("A job with the same pid already exist!");
                canAdd = false;
            }
        }
        if (canAdd == true) {
            Job newJob = new Job(pid, size);
            jobList.add(newJob);
            addList.add(newJob);
        }
    }

    public static void showJobs() {
        for (Job jobs : jobList) {
            System.out.println(jobs.toString());
        }
    }

    public static void showMemoryList() {
        Segment temp = head;
        System.out.print(temp.toString());
        while (temp.getNext() != null) {
            temp = temp.getNext();
            System.out.print(temp.toString());
        }
        System.out.println();
    }

    public static boolean firstFit(String id) {
        boolean added = false;
        Job tempJob = getJob(id);
        if (tempJob == null) {
            return added;
        }

        Segment temp = head;
        if ((head.getPid() == 0) && (tempJob.getSize() <= head.getLength())) {
            added = addAndSwap(tempJob, head);
            return added;
        }
        while (temp.getNext() != null) {
            temp = temp.getNext();
            if ((temp.getPid() == 0) && (tempJob.getSize() <= temp.getLength())) {
                added = addAndSwap(tempJob, temp);
                return added;
            }
        }
        return added;
    }

    public static boolean nextFit(String id) {
        boolean added = false;
        Job tempJob = getJob(id);
        if (tempJob == null) {
            return added;
        }

        Segment temp = current;
        if ((current.getPid() == 0) && (tempJob.getSize() <= current.getLength())) {
            added = addAndSwap(tempJob, current);
            return added;
        }
        while (temp.getNext() != null) {
            temp = temp.getNext();
            if ((temp.getPid() == 0) && (tempJob.getSize() <= temp.getLength())) {
                added = addAndSwap(tempJob, temp);
                return added;
            }
        }
        return added;
    }

    public static boolean bestFit(String id) {
        boolean added = false;
        Job tempJob = getJob(id);
        if (tempJob == null) {
            return added;
        }

        Segment temp = head;
        Segment bestfit = null;
        try {
            if ((head.getPid() == 0) && (tempJob.getSize() <= head.getLength())) {
                bestfit = head;
            } else {
                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    if ((temp.getPid() == 0) && (tempJob.getSize() <= temp.getLength())) {
                        bestfit = temp;
                        break;
                    }
                }
            }
            temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
                if ((temp.getPid() == 0) && (tempJob.getSize() <= temp.getLength()) && (temp.getLength() < bestfit.getLength())) {
                    bestfit = temp;
                }
            }
            added = addAndSwap(tempJob, bestfit);
        } catch (NullPointerException e) {
            return false;
        }

        return added;
    }

    public static boolean worstFit(String id) {
        boolean added = false;
        Job tempJob = getJob(id);
        if (tempJob == null) {
            return added;
        }

        Segment temp = head;
        Segment worstfit = null;
        try {
            if ((head.getPid() == 0) && (tempJob.getSize() <= head.getLength())) {
                worstfit = head;
            } else {
                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    if ((temp.getPid() == 0) && (tempJob.getSize() <= temp.getLength())) {
                        worstfit = temp;
                        break;
                    }
                }
            }
            temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
                if ((temp.getPid() == 0) && (tempJob.getSize() <= temp.getLength()) && (temp.getLength() > worstfit.getLength())) {
                    worstfit = temp;
                }
            }
            added = addAndSwap(tempJob, worstfit);
        }catch(NullPointerException e){
            return false;
        }
        return added;
    }

    public static boolean deAllocate(String id) {
        int pid = 0;
        boolean removed = false;
        try {
            pid = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            System.err.println("Wrong input format");
            return false;
        }
        Segment temp = head;
        if (head.getPid() == pid) {
            addList.add(new Job(head.getPid(), head.getLength()));
            head.setPid(0);
            removed = true;
        } else {
            while (temp.getNext() != null) {
                temp = temp.getNext();
                if (temp.getPid() == pid) {
                    addList.add(new Job(temp.getPid(), temp.getLength()));
                    temp.setPid(0);
                    removed = true;
                }
            }
        }
        fixList();
        fixList();
        return removed;
    }

    private static void fixList() {
        Segment temp = head;
        while (temp.getNext() != null) {
            if (temp.getPid() == 0 && temp.getNext().getPid() == 0) {
                temp.setLength(temp.getLength() + temp.getNext().getLength());
                temp.setNext(temp.getNext().getNext());
                break;
            }
            temp = temp.getNext();

        }
    }

    private static boolean addAndSwap(Job tempJob, Segment temp) {
        boolean added = false;
        if (tempJob.getSize() == temp.getLength()) {
            temp.setPid(tempJob.getPid());
            temp.setLength(tempJob.getSize());
            added = true;
            current = temp.getNext();
            return added;
        } else {
            int holelength = temp.getLength() - tempJob.getSize();
            int holestart = temp.getStart() + tempJob.getSize();
            temp.setPid(tempJob.getPid());
            temp.setLength(tempJob.getSize());
            Segment newhole = new Segment(0, holestart, holelength, temp.getNext());
            temp.setNext(newhole);
            current = newhole;
            added = true;
            return added;
        }
    }

    private static Job getJob(String id) throws ConcurrentModificationException {
        Job tempJob = null;
        int pid = 0;
        try {
            pid = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            System.err.println("Wrong input format");
            return null;
        }
        for (int i = 0; i < addList.size(); i++) {
            if (pid == addList.get(i).getPid()) {
                tempJob = addList.get(i);
                addList.remove(i);
            }
        }
        return tempJob;
    }
}
