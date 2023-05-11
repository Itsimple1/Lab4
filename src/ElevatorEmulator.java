import java.util.*;
import java.lang.Math;
class ElInfo{
    int floor;
    int direction;
    int nextdirection;
    public ElInfo(int i1, int i2, int i3){
        floor = i1;
        direction = i2;
        nextdirection = i3;
    }
}  // Class for save data about elevator
class Request {
    int floor;
    int direction;
    int need_floor;

    public Request(int max_floor) {
        floor = (int) ((Math.random() * (max_floor - 1)) + 1);
        direction = (int) ((Math.random() * (max_floor - 2)) - floor + 1);
        if (direction >= 0) ++direction;
        need_floor = floor + direction;
    }
}
class Requests {
    int floors;
    int count_req;
    ArrayList<Request> reqs;
    public Requests(int fl, int cr){
        floors = fl;
        count_req = cr;
        reqs = new ArrayList<Request>();
    }
    public void AddRequests(){
        synchronized (this) {
            if (reqs.size() < 5) {
                for (int j = 0; j < count_req; ++j) {
                    reqs.add(new Request(floors));
                }
            }
            String s = "Current requests:\nFloor   NeedFloor";
            for (Request req : reqs)
                s += "\n" + Integer.toString(req.floor) + "       " + Integer.toString(req.need_floor);
            System.out.println(s+"\n");
        }
    }
    public void GetTask(Elevator l, int el2floor, int el2direction, int nextdirection){
        synchronized (this){
            if (l.direction == 0 && l.next_direction != 0) {
                l.direction  = l.next_direction;
                l.next_direction = 0;
                return;
            }

            if (reqs.size() == 0)
                return;

            if (reqs.size()==1 && Math.abs(reqs.get(0).floor-l.floor) > Math.abs(reqs.get(0).floor-el2floor) &&
                    el2direction == 0 || (el2direction > 0 && reqs.get(0).floor - el2floor >= 0 &&
                    (reqs.get(0).direction > 0 && el2direction+el2floor>=reqs.get(0).floor && nextdirection>=0
                            || el2direction+el2floor==reqs.get(0).floor && nextdirection<=0 && reqs.get(0).direction<0) ||
                    el2direction < 0 && reqs.get(0).floor - el2floor <= 0 && (reqs.get(0).direction < 0 && el2direction+el2floor<=reqs.get(0).floor && nextdirection<=0
                            || el2direction+el2floor==reqs.get(0).floor && nextdirection>=0 && reqs.get(0).direction>0))){
                return;
            }
            String s = "Elevator: " + Integer.toString(l.number) + " take request(s):\nFloor NeedFloor";
            if (l.direction == 0) {
                int min_dif_floor = 100000;
                int dif_floor = 0;
                int next_dir = 0;
                boolean flag = false;
                for (Request req : reqs) {

                    dif_floor = req.floor - l.floor;
                    if (Math.abs(dif_floor) < Math.abs(min_dif_floor)) {
                        min_dif_floor = dif_floor;
                        next_dir = req.direction;
                    }
                    else if (dif_floor == min_dif_floor && (req.direction<0 && next_dir<0 || req.direction>0 && next_dir>0)) {
                        next_dir = next_dir < 0 ? Math.min(req.direction, next_dir) : Math.max(req.direction, next_dir);
                    }
                }
                if (min_dif_floor == 0) {
                    for (int i = 0; i < reqs.size(); ++i) {
                        if (reqs.get(i).floor - l.floor == 0 && (next_dir > 0 && reqs.get(i).direction > 0 || next_dir < 0 && reqs.get(i).direction < 0)) {
                            flag = true;
                            s += "\n" + Integer.toString(reqs.get(i).floor) + "       " + Integer.toString(reqs.get(i).need_floor);
                            reqs.remove(reqs.get(i));
                        }
                    }
                    l.direction = next_dir;
                } else {
                    for (int i = 0; i < reqs.size(); ++i) {
                        if (reqs.get(i).floor - l.floor == min_dif_floor) {
                            flag = true;
                            s += "\n" + Integer.toString(reqs.get(i).floor) + "       " + Integer.toString(reqs.get(i).need_floor);
                            reqs.remove(reqs.get(i));
                        }
                    }
                    l.direction = min_dif_floor;
                    l.next_direction = next_dir;
                }
                if (flag) {
                    System.out.println(s+"\n");
                }
            }
            else {
                boolean flag = false;
                int dir = l.direction;
                if (dir > 0) {
                    for (int i = 0; i < reqs.size(); ++i) {
                        if (l.floor == reqs.get(i).floor && reqs.get(i).direction > 0) {
                            flag = true;
                            s += "\n" + Integer.toString(reqs.get(i).floor) + "       " + Integer.toString(reqs.get(i).need_floor);
                            dir = Math.max(dir, reqs.get(i).direction);
                            reqs.remove(reqs.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < reqs.size(); ++i) {
                        if (l.floor == reqs.get(i).floor && reqs.get(i).direction < 0) {
                            flag = true;
                            s += "\n" + Integer.toString(reqs.get(i).floor) + "       " + Integer.toString(reqs.get(i).need_floor);
                            dir = Math.min(dir, reqs.get(i).direction);
                            reqs.remove(reqs.get(i));
                        }
                    }
                }
                l.direction = dir;
                if (flag) {
                    System.out.println(s+"\n");
                }
            }
        }
    }
    public ArrayList<Request> GetReqs(){
        return new ArrayList<>(reqs);
    }
}  // Class for work woth requests in multithread
class Elevator {
    int number = 0;
    int floor = 1;
    int direction = 0;
    int next_direction = 0;
    public void Up(){
        synchronized (this) {
            --direction;
            ++floor;
        }
    }
    public void Down(){
        synchronized (this) {
            ++direction;
            --floor;
        }
    }
    public Elevator(int num) {
        number = num;
    }
    public ElInfo GetInfo() {
        synchronized (this)
        {
            return new ElInfo(floor, direction, next_direction);
        }
    }
}  // Emulation of elevator
class ElevatorThread extends Thread{
    Requests Reqs;
    Elevator l;
    Elevator SecondEl;
    public ElevatorThread(Elevator elevator, Elevator l2, Requests req){
        l = elevator;
        SecondEl = l2;
        Reqs = req;
    }
    public void run(){
        ElInfo SecondElInfo;
        while (!this.isInterrupted())
        {
            SecondElInfo = SecondEl.GetInfo();
            Reqs.GetTask(l, SecondElInfo.floor, SecondElInfo.direction, SecondElInfo.direction);
            try{
                if (l.direction < 0)
                {
                    while (l.direction != 0) {
                        SecondElInfo = SecondEl.GetInfo();
                        Reqs.GetTask(l, SecondElInfo.floor, SecondElInfo.direction, SecondElInfo.direction);
                        sleep(1000);
                        l.Down();
                        System.out.println("Elevator: " + l.number + ". Floor: " + l.floor + ". Direction: down\n");
                    }
                }
                else if (l.direction > 0)
                {
                    while (l.direction != 0) {
                        SecondElInfo = SecondEl.GetInfo();
                        Reqs.GetTask(l, SecondElInfo.floor, SecondElInfo.direction, SecondElInfo.direction);
                        sleep(1000);
                        l.Up();
                        System.out.println("Elevator: " + l.number + ". Floor: " + l.floor + ". Direction: up\n");
                    }
                }
            } catch (InterruptedException e){
                System.out.println("Elevator " + l.number + " has stopped");
                return;
            }
        }
    }
}

public class ElevatorEmulator {
    int floors;
    int interval;
    int count_req;
    int limit_time_work;
    public ElevatorEmulator(){
        floors = 20;
        interval = 3;
        count_req = 1;
        limit_time_work = 20;
    }
    public void run() {
        int CountLoop = limit_time_work/interval + 1;
        long IntLoop = interval * 1000;

        Requests Reqs = new Requests(floors, count_req);

        Elevator l1 = new Elevator(1);
        Elevator l2 = new Elevator(2);

        ElevatorThread t1 = new ElevatorThread(l1, l2, Reqs);
        t1.start();
        ElevatorThread t2 = new ElevatorThread(l2, l1, Reqs);
        t2.start();

        if (limit_time_work > 0) {
            for (int i = 0; i < CountLoop; ++i)
            {
                Reqs.AddRequests();
                try {
                    Thread.sleep(IntLoop);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            while (true)
            {
                Reqs.AddRequests();
                try {
                    Thread.sleep(IntLoop);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        t1.interrupt();
        t2.interrupt();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}