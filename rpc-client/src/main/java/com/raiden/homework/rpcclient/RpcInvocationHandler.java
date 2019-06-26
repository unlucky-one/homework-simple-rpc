package com.raiden.homework.rpcclient;

import com.raiden.homewiork.rpcapi.RpcRequest;
import com.raiden.homewiork.rpcapi.Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class RpcInvocationHandler implements InvocationHandler {

    int port ;
    RpcInvocationHandler(int port){
        this.port=port;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethod(method.getName());
        request.setParam(args);
        return remoteSend(request);
    }

    Object remoteSend(RpcRequest request) {
        ObjectOutputStream outputStream=null;
        ObjectInputStream inputStream=null;
        Socket socket=null;
        try {
            socket=new Socket("localhost",port);
            outputStream=new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.writeObject(null);
            outputStream.flush();
            inputStream=new ObjectInputStream(socket.getInputStream());
            return inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.closeStream(outputStream);
            Utils.closeStream(inputStream);
            Utils.closeStream(socket);
        }
        return "";
    }

}
