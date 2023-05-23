import core.data.*;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class DataDiver {
    public static String data = "C://Users//aidan//Downloads//IPFLowData//data.json//data.json";
    public static DataSource ds = DataSource.connect(data);



    public static void main(String[] args) {
        //Loads the data source
        ds.load();

        //shows the structure of the JSON File
        testSource();

        //Initializing the array that data will be read into
        Traffic[] tr = fetchAll();

        //Processing data
        //finds all the protocol ID's from all the packets, so they can be identified later
        findProtocolIDs(tr);

        //Creating a DecimalFormat for displaying percentages later
        DecimalFormat df = new DecimalFormat("#.##");

        //Counts number of Destination and source IPv4 Hosts
        System.out.println("Number of Source Hosts: " + findDestIPAddresses(tr));
        System.out.println("Number of Destinations: " + findSourceIPAddresses(tr));

        //finds all the destinations that packets were sent to
        findExerciseSegment(tr);

        //code to find and modify data, don't mind the mess
        //how many ipv4 packets
        int ipv4Packet = 0;
        for (Traffic i : tr) {
            if (i.getIPVersion() == 4) {
                ipv4Packet++;
            }
        }

        //Print total number of packets
        System.out.println("Total IPv4 Packets: " + ipv4Packet);

        //Where packets are sent to
        int numBlueTeam = 0;
        int numInternet = 0;
        int numGlobal = 0;
        for (Traffic i : tr) {
            if (i.getDest().contains("blue-team")) {
                numBlueTeam++;
            } else if (i.getDest().contains("internet")) {
                numInternet++;
            } else if (i.getDest().contains("global")) {
                numGlobal++;
            }
        }

        //Print number of packets
        System.out.println("Number of Packets sent Blue Team: " + numBlueTeam +
                "\nNumber of packets sent to Internet: " + numInternet +
                "\nNumber of packets sent to Global: " + numGlobal);

        //Percentage of packets to Blue team / Internet / Global
        System.out.println("Percentage to Blue Team: " + df.format(((double) numBlueTeam / ipv4Packet) * 100));
        System.out.println("Percentage to Internet: " + df.format(((double) numInternet / ipv4Packet) * 100));
        System.out.println("Percentage to Global: " + df.format(((double) numGlobal / ipv4Packet) * 100));

        //turns the protocolIdentifier number into its Protocol String
        String protocol;
        int TCP = 0, UDP = 0, ICMP = 0, IGMP = 0, ESP = 0, unassigned = 0;
        for (Traffic i : tr) {
            protocol = identifyProtocol(i);
            if (protocol.equals("TCP")) {
                TCP++;
            } else if (protocol.equals("UDP")) {
                UDP++;
            } else if (protocol.equals("ICMP")) {
                ICMP++;
            } else if (protocol.equals("IGMP")) {
                IGMP++;
            } else if (protocol.equals("ESP")) {
                ESP++;
            } else if (protocol.equals("Unassigned")) {
                unassigned++;
            }
        }

        //Print number and percentage of different protocols
        //Total number of different protocols
        System.out.println("TCP: " + TCP +
                "\nUDP: " + UDP +
                "\nICMP: " + ICMP +
                "\nIGMP: " + IGMP +
                "\nESP: " + ESP +
                "\nUnassigned: " + unassigned);

        //Percentage of different protocols
        System.out.println("Percentage TCP: " + df.format((double) TCP / ipv4Packet * 100) +
                "\nPercentage UDP: " + df.format((double) UDP / ipv4Packet * 100) +
                "\nPercentage ICMP: " + df.format((double) ICMP / ipv4Packet * 100) +
                "\nPercentage IGMP: " + df.format((double) IGMP / ipv4Packet * 100) +
                "\nPercentage ESP: " + df.format((double) ESP / ipv4Packet * 100) +
                "\nPercentage Unassigned: " + df.format((double) unassigned / ipv4Packet * 100));

        //End processing
    }




    //reads the data from the dataset and puts it into an array
    public static Traffic[] fetchAll(){
        return ds.fetchArray("Traffic", "sourceIPv4Address", "destinationIPv4Address", "timestamp", "exercise_dst_ipv4_segment", "ipVersion", "protocolIdentifier");
    }

    //Identifies protocols by their ID's
    public static String identifyProtocol(Traffic t){
        int idNum = t.getProtocolID();
        switch(idNum) {
            case 1:
                return "ICMP";
            case 2:
                return "IGMP";
            case 6:
                return "TCP";
            case 17:
                return "UDP";
            case 50:
                return "ESP";
            case 249:
                return "Unassigned";
            default:
                return "other";
        }
    }

    //loops through traffic array and adds protocolIdentifier to idArr array if it is not already in idArr
    public static ArrayList<Integer> findProtocolIDs(Traffic[] tr){
        int id;
        boolean exist;
        ArrayList<Integer> idArr = new ArrayList<>();
        for (Traffic i : tr) {
            exist = false;
            id = i.getProtocolID();
            for (int j : idArr) {
                if (j == id) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                idArr.add(id);
            }
        }
        return idArr;
    }

    //same is findProtocolIDs but for destination IP addresses
    public static int findDestIPAddresses(Traffic[] tr){
        String ip;
        boolean exist;
        ArrayList<String> ipArr = new ArrayList<>();
        for (Traffic i : tr) {
            exist = false;
            ip = i.getDestIP();
            for (String j : ipArr) {
                if (j.equals(ip)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                ipArr.add(ip);
            }
        }
        return ipArr.size();
    }

    //same as findProtocolIDs but for source IP addresses
    public static int findSourceIPAddresses(Traffic[] tr){
        String ip;
        boolean exist;
        ArrayList<String> ipArr = new ArrayList<>();
        for (Traffic i : tr) {
            exist = false;
            ip = i.getSourceIP();
            for (String j : ipArr) {
                if (j.equals(ip)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                ipArr.add(ip);
            }
        }
        return ipArr.size();
    }

    //same as findProtocolIDs but for exercise destination id
    public static ArrayList<String> findExerciseSegment(Traffic[] tr){

        String ex;
        boolean exist;
        ArrayList<String> exArr = new ArrayList<>();
        for (Traffic i : tr) {
            exist = false;
            ex = i.getDest();
            for (String j : exArr) {
                if (j.equals(ex)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                exArr.add(ex);
            }
        }
        return exArr;
    }


    public static void testSource(){
        ds.printUsageString();
    }
}