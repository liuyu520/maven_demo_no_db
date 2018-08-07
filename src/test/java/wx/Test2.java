package wx;

import com.kunlunsoft.wxcp.mp.aes.AesException;
import com.kunlunsoft.wxcp.mp.aes.WXBizMsgCrypt;
import org.junit.Test;

public class Test2 {
    /***
     * 2018/7/23
     */
    @Test
    public final void test_Descrpt() {
        WXBizMsgCrypt wxcpt = null;
        try {
//            wxcpt = new WXBizMsgCrypt("LWGlsuxr", "NDa2M6bTU9Qm8YCCvJvMVXExwBQunAU52ykGDjvuoml", "ww92653858095c7bd4");
            wxcpt = new WXBizMsgCrypt("RKpp9U", "W5itiVekWSy2usL9taeaoDSWJJAUji5UemtBa1IPOTS", "ww92653858095c7bd4", "");
            try {
                String sEchoStr = wxcpt.decrypt("jrS/g5LKT7erKl5yZb/puSDJJhTUxV7onIZcHOVaFlkLbc1VbuO052twSPlGo82clXFWTghRwP3eF0D0pv36CA==");
//            String decrypted= wxBizMsgCrypt.decrypt(echostr);
                System.out.println("sEchoStr :" + sEchoStr);
            } catch (AesException e) {
                e.printStackTrace();
            }
        } catch (AesException e) {
            e.printStackTrace();
        }
    }
}
