public class Traffic {
    private String sourceIP;
    private String destIP;
    private String timestamp;
    private String exerciseDest;
    private int ipVersion;

    private int protocolID;

    public Traffic(String s, String d, String t, String e, String ip, String p){
        sourceIP = s;
        destIP = d;
        timestamp = t;
        exerciseDest = e;
        ipVersion = Integer.parseInt(ip);
        protocolID = Integer.parseInt(p);
    }

    //getters
    public String getSourceIP(){
        return sourceIP;
    }
    public String getDestIP(){
        return destIP;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public String getDest(){
        return exerciseDest;
    }

    public int getIPVersion(){
        return ipVersion;
    }

    public int getProtocolID(){
        return protocolID;
    }

    //toString
    public String toString(){
        return "Source IP: " +sourceIP+ "\nDestination IP: " +destIP+ "\nTeam: " +exerciseDest;
    }
}
