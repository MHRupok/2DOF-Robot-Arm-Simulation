/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myvreplib;

import coppelia.CharWA;
import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;

public class MyVrep {

    public remoteApi vrep;
    public int clientID;

    public MyVrep(String ip, int port) throws Exception {
        System.out.println("connecting " + ip + " on " + port + " port");
        vrep = new remoteApi();
        vrep.simxFinish(-1); // just in case, close all opened connections
        clientID = vrep.simxStart(ip, port, true, true, 5000, 5);
        if (clientID == -1) {
//            System.out.println("Failed!");
            throw new Exception("Cant' Connect");
        }
        System.out.println("success: connected");
    }

    public void startSimulation() {
        vrep.simxStartSimulation(clientID, vrep.simx_opmode_blocking);
    }

    public int copyObjectHId = -1;

    public IntW copyObject_getH(IntW h) {
        IntW handle = new IntW(0);

        IntWA old = new IntWA(1);
        old.setSingleItem(h.getValue());
        IntWA n = new IntWA(1);
        n.setSingleItem(handle.getValue());

        int code = vrep.simxCopyPasteObjects(this.clientID, old, n, vrep.simx_opmode_oneshot_wait);

        int sdo = n.getArray()[0];
        copyObjectHId = sdo;
        return handle;
    }

    public int copyObject(float x, float y, float z, IntW h) {
        IntW handle = new IntW(0);

        IntWA old = new IntWA(1);
        old.setSingleItem(h.getValue());
        IntWA n = new IntWA(1);
        n.setSingleItem(handle.getValue());

        int code = vrep.simxCopyPasteObjects(this.clientID, old, n, vrep.simx_opmode_oneshot_wait);

        int sdo = n.getArray()[0];
        copyObjectHId = sdo;
        return this.setObjectPos(x, y, z, sdo);
    }

    public int copyObjectRef(float x, float y, float z, IntW h, int refval) {
        IntW handle = new IntW(0);

        IntWA old = new IntWA(1);
        old.setSingleItem(h.getValue());
        IntWA n = new IntWA(1);
        n.setSingleItem(handle.getValue());

        int code = vrep.simxCopyPasteObjects(this.clientID, old, n, vrep.simx_opmode_oneshot_wait);

        int sdo = n.getArray()[0];
        copyObjectHId = sdo;
        return this.setObjectPos(x, y, z, sdo, refval);
    }

    public int setObjectParent(int obj, int parent) {
        int code = vrep.simxSetObjectParent(clientID, obj, parent, false, vrep.simx_opmode_blocking);
        return code;
    }

    public int setObjectPos(float x, float y, float z, int hval, int refval) {
        FloatWA pos = new FloatWA(3);
        pos.initPos(x, y, z);
        int code = vrep.simxSetObjectPosition(clientID, hval, refval, pos, vrep.simx_opmode_oneshot_wait);

        return code;
    }

    public int setObjectPos(float x, float y, float z, int hval) {
        FloatWA pos = new FloatWA(3);
        pos.initPos(x, y, z);
        int code = vrep.simxSetObjectPosition(clientID, hval, -1, pos, vrep.simx_opmode_oneshot_wait);

        return code;
    }

    public int setObjectPos(float x, float y, float z, IntW h) {
        return this.setObjectPos(x, y, z, h.getValue());
    }

    public int setObjectOrientation(float x, float y, float z, int hval) {
        FloatWA ea = new FloatWA(3);
        ea.initPos(x, y, z);
        int code = vrep.simxSetObjectOrientation(clientID, hval, hval, ea, vrep.simx_opmode_oneshot_wait);
        return code;
    }

    public int setObjectOrientation(float x, float y, float z, int hval, int ref) {
        FloatWA ea = new FloatWA(3);
        ea.initPos(x, y, z);
        int code = vrep.simxSetObjectOrientation(clientID, hval, ref, ea, vrep.simx_opmode_oneshot_wait);
        return code;
    }

    public int setObjectOrientation(float x, float y, float z, IntW h) {
        return this.setObjectOrientation(x, y, z, h.getValue());
    }

    public int addDummy(float x, float y, float z) {
        return this.addDummy(x, y, z, 0.03f);
    }
    IntW lastH = null;

    public IntW getLastH() {
        return lastH;
    }

    public int addDummy(float x, float y, float z, float r) {
        IntW dh = new IntW(0);
        CharWA cs = new CharWA("RED");
        int ec = vrep.simxCreateDummy(clientID, r, null, dh, vrep.simx_opmode_oneshot);
        lastH = dh;

        FloatWA pos = new FloatWA(3);
        pos.initPos(x, y, z);

        ec = vrep.simxSetObjectPosition(clientID, dh.getValue(), -1, pos, vrep.simx_opmode_oneshot_wait);

        return ec;
    }

    public IntW getObjHandle(String name) {
        IntW handle = new IntW(0);

        int code = vrep.simxGetObjectHandle(clientID, name, handle, vrep.simx_opmode_blocking);
        if (code == 0) {
            return handle;
        }
        System.out.println("error=" + code);
        return null;
    }

    public IntW[] get_handles(String[] names) {
        IntW[] handles = new IntW[names.length];
        for (int i = 0; i < names.length; i++) {
            handles[i] = getObjHandle(names[i]);
            if (handles[i] == null) {
                System.out.println(names[i] + " failed");
            }
        }
        return handles;
    }

    public void sendMsg(String msg) {
        vrep.simxAddStatusbarMessage(clientID, msg, vrep.simx_opmode_oneshot);
    }

    public int setJointPos(IntW handle, float deg) {
        sendMsg("move to " + deg);
        int code = vrep.simxSetJointTargetPosition(clientID, handle.getValue(), deg, vrep.simx_opmode_streaming);

        return code;
    }

    public int setJointPosDeg(IntW handle, double d) {
        sendMsg("move to " + d);
        float deg = (float) (d * Math.PI / 180);
//        System.out.println("rad=" + deg);
//        int code = vrep.simxSetJointTargetPosition(clientID, handle.getValue(), deg, vrep.simx_opmode_streaming);
        int code = vrep.simxSetJointTargetPosition(clientID, handle.getValue(), deg, vrep.simx_opmode_oneshot);
        return code;
    }

    public int setJointPosDeg(int hval, double d) {
        sendMsg("move to " + d);
        float deg = (float) (d * Math.PI / 180);
//        System.out.println("rad=" + deg);
//        int code = vrep.simxSetJointTargetPosition(clientID, handle.getValue(), deg, vrep.simx_opmode_streaming);
        int code = vrep.simxSetJointTargetPosition(clientID, hval, deg, vrep.simx_opmode_oneshot);
        return code;
    }

    public double getJointPosDeg(IntW handle) {
        FloatW p = new FloatW(2.1f);

        int code = vrep.simxGetJointPosition(clientID, handle.getValue(), p, vrep.simx_opmode_oneshot_wait);
        double ps = p.getValue() * 180 / Math.PI;
//        System.out.println("get: code="+code+" p="+ps +" deg="+ps*180/Math.PI);

        return ps;
    }

    public FloatWA getObjectPosition(IntW handle) {
        FloatWA position = new FloatWA(0);
        int ec = vrep.simxGetObjectPosition(clientID, handle.getValue(), -1, position, vrep.simx_opmode_blocking);
//        System.out.println("ec=" + ec);
        return position;
    }

    public FloatW getJointTorque(IntW handle) {
        FloatW p=new FloatW(2.1f);
        int ec=vrep.simxGetJointForce(clientID, handle.getValue(), p, vrep.simx_opmode_blocking);
//        System.out.println("ec=" + ec);
        return p;
    }

    public int setObjectPosition(IntW h, FloatWA pos) {
        int ec = vrep.simxSetObjectPosition(clientID, h.getValue(), -1, pos, vrep.simx_opmode_blocking);
//        System.out.println("ec=" + ec);
        return ec;
    }

    public FloatWA getFSR(IntW h) {
        FloatWA forceVector = new FloatWA(0);
        FloatWA torqueVector = new FloatWA(0);
        IntW state = new IntW(0);
        int ec = vrep.simxReadForceSensor(clientID, h.getValue(), state, forceVector, torqueVector, vrep.simx_opmode_blocking);
        return forceVector;
    }

    public void readFSR() {
        IntW hl = getObjHandle("NAO_LFsrRL");
        IntW hr = getObjHandle("NAO_RFsrRR");

        FloatWA forceVector = new FloatWA(0);
        FloatWA torqueVector = new FloatWA(0);

        FloatWA forceVectorR = new FloatWA(0);
        FloatWA torqueVectorR = new FloatWA(0);

        IntW stateL = new IntW(0);
        IntW stateR = new IntW(0);
        int ec = vrep.simxReadForceSensor(clientID, hl.getValue(), stateL, forceVector, torqueVector, vrep.simx_opmode_blocking);
        int ec2 = vrep.simxReadForceSensor(clientID, hr.getValue(), stateR, forceVectorR, torqueVectorR, vrep.simx_opmode_blocking);
        System.out.println("ec=" + ec + " ec2=" + ec2);

        float[] ss = forceVector.getArray();
        float[] sr = forceVectorR.getArray();

        System.out.println("SL: " + ss[0] + " ," + ss[1] + " ," + ss[2]);
        System.out.println("SR: " + sr[0] + " ," + sr[1] + " ," + sr[2]);
//        String str = Arrays.toString(ss);
//        System.out.println("get array............... = " + ss[2] + "  " + ssR[2]);

    }

    public int setJointVel(IntW handle, float vel) {
        int code = vrep.simxSetJointTargetVelocity(clientID, handle.getValue(), vel, vrep.simx_opmode_blocking);
        return code;
    }

    public void print_dist_info(String name1, String name2) {
        System.out.println("\nget_info for " + name1 + " " + name2);
        IntW h1 = getObjHandle(name1);
        IntW h2 = getObjHandle(name2);

        FloatWA p1 = getObjectPosition(h1);
        FloatWA p2 = getObjectPosition(h2);
        this.print_pos(p1.getArray());
        this.print_pos(p2.getArray());
        myvreplib.Point pp1 = new myvreplib.Point(p1.getArray()[0], p1.getArray()[1], p1.getArray()[2], 0, 0, 0);
        myvreplib.Point pp2 = new myvreplib.Point(p2.getArray()[0], p2.getArray()[1], p2.getArray()[2], 0, 0, 0);

        double d = pp2.distanceTo(pp1);
        System.out.println("d=" + d);
        for (int i = 0; i < 3; i++) {
            double dt = p1.getArray()[i] - p2.getArray()[i];
            System.out.println("i=" + i + " dt=" + dt);
        }
        System.out.println("");
    }

    void print_pos(float[] v) {
        System.out.print("pos=");
        for (int i = 0; i < v.length; i++) {
            System.out.print(" " + v[i]);
        }
        System.out.println("");
    }

    public void close() {
        // stop the simulation:
        vrep.simxStopSimulation(clientID, vrep.simx_opmode_blocking);
        // Now close the connection to V-REP:	
        vrep.simxFinish(clientID);
    }
}
