# AndroidRTSPPlayer
play rtsp with vlclib on android device

try{
            EventHandler em = EventHandler.getInstance();
            em.addHandler(handler);

            mLibVLC = Util.getLibVlcInstance();

            if (mLibVLC != null) {
//              String pathUri = "rtsp://192.168.1.1/MJPG?W=640&H=360&Q=50&BR=3000000";  //流媒体地址
//              String pathUri = "file:////sdcard/DCIM/Camera/test.mp4";   //本地地址
                mLibVLC.playMyMRL(pathUri);
            }
        } catch (LibVlcException e) {
            e.printStackTrace();
        }
    }