/*
 * EtherType.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

package pktanalyzer;

/**
 * Represents an EtherType value
 * Has provisions for some notable protocols and also the alternative "length"
 * field if value is lower than 1500. The label is "UNDEFINED" if the value
 * is between 1501 and 1535 inclusive.
 *
 * Reference: https://en.wikipedia.org/wiki/EtherType#Values
 * 
 * @author Tanishq Jain <tj3989@cs.rit.edu>
 */
public class EtherType {
    private int val;
    private String label;

    EtherType(int val) {
        this.val = val;

        if (val >= 0 && val <= 1500) {
            label = "LENGTH";
        } else if (val >= 1536) {
            switch (val) {
                case 0x0800:
                    label = "IP";
                    break;

                case 0x0806:
                    label = "ARP";
                    break;

                case 0x0842:
                    label = "WAKE_ON_LAN";
                    break;

                case 0x22F0:
                    label = "AVTP";
                    break;

                case 0x22F3:
                    label = "IETF_TRILL_PROTO";
                    break;

                case 0x22EA:
                    label = "STREAM_RES_PROTO";
                    break;

                case 0x6002:
                    label = "DEC_MOP_RC";
                    break;

                case 0x6003:
                    label = "DECNET_IV_DNA";
                    break;

                case 0x6004:
                    label = "DEC_LAT";
                    break;

                case 0x8035:
                    label = "RARP";
                    break;

                case 0x809B:
                    label = "APPLETALK";
                    break;

                case 0x80F3:
                    label = "AARP";
                    break;

                case 0x8100:
                    label = "VLAN_TAG";
                    break;

                case 0x8102:
                    label = "SLPP";
                    break;

                case 0x8103:
                    label = "VLACP";
                    break;

                case 0x8137:
                    label = "IPX";
                    break;

                case 0x8204:
                    label = "QNX_QNET";
                    break;

                case 0x86DD:
                    label = "IPV6";
                    break;

                case 0x8808:
                    label = "ETH_FLOW_CONT";
                    break;

                case 0x8809:
                    label = "ETH_SLOW_PROTO";
                    break;

                case 0x8819:
                    label = "COBRANET";
                    break;

                case 0x8847:
                    label = "MPLS_UNICAST";
                    break;

                case 0x8848:
                    label = "MPLS_MULTICAST";
                    break;

                case 0x8863:
                    label = "PPPOE_DISCOVERY";
                    break;

                case 0x8864:
                    label = "PPPOE_SESSION";
                    break;

                case 0x887B:
                    label = "HOMEPLUG";
                    break;

                case 0x888E:
                    label = "EAP_LAN";
                    break;

                case 0x8892:
                    label = "PROFINET";
                    break;

                case 0x889A:
                    label = "HYPERSCI";
                    break;

                case 0x88A2:
                    label = "ATA_ETH";
                    break;

                case 0x88A4:
                    label = "ETHCAT_PROTO";
                    break;

                case 0x88A8:
                    label = "VLAN_S_TAG";
                    break;

                case 0x88AB:
                    label = "ETH_POWERLINK";
                    break;

                case 0x88B8:
                    label = "GOOSE";
                    break;

                case 0x88B9:
                    label = "GSE";
                    break;

                case 0x88BA:
                    label = "SV";
                    break;

                case 0x88BF:
                    label = "MIKROTIK_RMON";
                    break;

                case 0x88CC:
                    label = "LLDP";
                    break;

                case 0x88CD:
                    label = "SERCOS_III";
                    break;

                case 0x88E1:
                    label = "HOMEPLUG_PHY";
                    break;

                case 0x88E3:
                    label = "MR_PROTO";
                    break;

                case 0x88E5:
                    label = "MAC_SEC";
                    break;

                case 0x88E7:
                    label = "PBB";
                    break;

                case 0x88F7:
                    label = "PTP";
                    break;

                case 0x88F8:
                    label = "NC_SI";
                    break;

                case 0x88FB:
                    label = "PRP";
                    break;

                case 0x8902:
                    label = "CFM/OAM";
                    break;

                case 0x8906:
                    label = "FCOE";
                    break;

                case 0x8914:
                    label = "FCOE_INIT_PROTO";
                    break;

                case 0x8915:
                    label = "ROCE";
                    break;

                case 0x891D:
                    label = "TTETH_PROTO_CONT_FRAME";
                    break;

                case 0x893a:
                    label = "IEEE_1905_PROTO";
                    break;

                case 0x892F:
                    label = "HSR";
                    break;

                case 0x9000:
                    label = "ETH_CONF_TESTING_PROTO";
                    break;

                case 0xF1C1:
                    label = "REDUNDANCY_TAG";
                    break;

                default:
                    label = "UNKNOWN";
            }
        } else {
            label = "UNDEFINED";
        }
    }

    public int value() {
        return val;
    }

    public String label() {
        return label;
    }
}