/*
 * Copyright (c) 2004 - 2018, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import java.io.ByteArrayOutputStream;
import javax.persistence.EntityManager;
import javax.sound.sampled.*;

import java.io.Closeable;
import java.io.File;


/**
 *
 *
 * @author eugene
 */
public class MicrophoneService extends NodeService {
    private File audioFile = new File("sound.wav");
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private final AudioFormat format = new AudioFormat(8000.0F, 16, 1, true, true);
    private TargetDataLine microphone;
    private SourceDataLine speakers;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private long bytesReaded = 0L;
    private final int CHUNK_SIZE = 1024;
    byte buffer[] = new byte[10000];
    private byte[] microphoneBuffer;

//    private final DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
//    private final DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
    public MicrophoneService(ServiceData data, EntityManager manager) {
        super(data, manager);
        try {
//            microphone = AudioSystem.getTargetDataLine(format);
            microphone = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, format));
            microphoneBuffer = new byte[microphone.getBufferSize() / 5];
            speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
//            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
//            for (Mixer.Info info : mixerInfos) {
//                Mixer m = AudioSystem.getMixer(info);
//                Line.Info[] lineInfos = m.getSourceLineInfo();
////                System.out.println("\nSourceLineInfo");
//                for (Line.Info lineInfo : lineInfos) {
//                    System.out.println(info.getName() + " --- " + lineInfo);
//                    Line line = m.getLine(lineInfo);
//                    System.out.println("\t----- " + line);
//                }
//                lineInfos = m.getTargetLineInfo();
////                System.out.println("\nTargetLineInfo");
//                for (Line.Info lineInfo : lineInfos) {
//                    System.out.println(m + " --- " + lineInfo);
//                    Line line = m.getLine(lineInfo);
//                    System.out.println("\t----- " + line);
//
//                }
//            }
        } catch (LineUnavailableException e) {
            System.err.println(e);
        }
    }

    @Override
    public void start() {
        super.start();
        try {
            microphone.open(format);
            microphone.start();
            speakers.open(format);
            speakers.start();
        } catch (LineUnavailableException e) {
            System.err.println(e);
        }
    }

    protected int value() {
        return (int) bytesReaded;
    }

    /**
     *
     */
    @Override
    public void run() {
        int len = microphone.read(buffer, 0, CHUNK_SIZE);
        bytesReaded += len;
        puts(bytesReaded);
        speakers.write(buffer, 0, len);
    }

    /**
     *
     */
    @Override
    public void stop() {
        speakers.drain();
        speakers.close();
        microphone.close();
        manager.getTransaction().begin();
        super.stop();
        manager.getTransaction().commit();
    }
    
    /**
     * Thread to capture the audio from the microphone and save it to a file
     */
//    private class CaptureThread implements Runnable {
//
//        /**
//         * Run method for thread
//         */
//        public void run() {
//            try {
//                AudioSystem.write(new AudioInputStream(microphone, format, start_time)));
//                //Will write to File until it's closed.
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

}
