package jvs.assignment6;

public class DetectedActivityParser {

    public static String parseActivityType(int i) {

            String type;

            switch (i){
                case 0:
                    type = "IN_VEHICLE";
                    break;
                case 1:
                    type = "ON_BICYCLE";
                    break;
                case 2:
                    type = "ON_FOOT";
                    break;
                case 3:
                    type = "STILL";
                    break;
                case 4:
                    type = "UNKNOWN";
                    break;
                case 5:
                    type = "TILTING";
                    break;
                case 6:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                default:
                    type = "ERROR";
                    break;
                case 7:
                    type = "WALKING";
                    break;
                case 8:
                    type = "RUNNING";
                    break;
                case 16:
                    type = "IN_ROAD_VEHICLE";
                    break;
                case 17:
                    type = "IN_RAIL_VEHICLE";
            }

            return type;
    }

    public static long parseActivityTypeToInterval(int i){
        long type;

        switch (i){
            case 0:
                type = 1000;
                break;
            case 1:
                type = 2*1000;
                break;
            case 2:
                type = 5*1000;
                break;
            case 3:
                type = 60*1000;
                break;
            case 4:
                type = -1;
                break;
            case 5:
                type = -1;
                break;
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                type = -1;
                break;
            case 7:
                type = 5*1000;
                break;
            case 8:
                type = 3*1000;
                break;
            case 16:
                type = 1000;
                break;
            case 17:
                type = 2*1000;
        }

        return type;
    }
}
