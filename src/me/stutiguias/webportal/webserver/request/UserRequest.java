/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class UserRequest extends HttpResponse {

    public UserRequest(WebPortal plugin) {
        super(plugin);
    }
    
    public void GetInfo(String HostAddress)  {
        LoggedPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        JSONObject json = new JSONObject();
            json.put("Name", authPlayer.WebSitePlayer.getName() );
            json.put("Admin", authPlayer.WebSitePlayer.getIsAdmin() );
            json.put("Money", FormatMoney(plugin.economy.getBalance( authPlayer.WebSitePlayer.getName() ) ) );
            json.put("Mail", plugin.db.getMail(authPlayer.WebSitePlayer.getName() ).size() );
            json.put("Avatarurl", plugin.Avatarurl + authPlayer.WebSitePlayer.getName() );
        Print(json.toJSONString(),"application/json");
    }
    
    public String FormatMoney(double num) {
        String[] format = plugin.Moneyformat.split("_");
        Locale locale = new Locale(format[0],format[1]);
        NumberFormat localeFormat = NumberFormat.getCurrencyInstance(locale);
        return localeFormat.format(num);
    }
    
    public void ItemLore(String SessionId,Map param) {
        int id = Integer.parseInt((String)param.get("id"));
        String metaCSV = plugin.db.GetItemInfo(id,"meta");
        String[] metas = metaCSV.split(",");
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (String meta : metas) {
            if (meta.startsWith("N[#$]")) {
                String metad = meta.replace("N[#$]", "").replaceAll("§\\w", "");
                json.put("Display Name",metad);
            } else {
                String metal = meta.replace("L[#$]", "").replaceAll("§\\w", "");
                jsonArray.add(metal);
            }
        }
        json.put("Lore",jsonArray.toJSONString());
        Print(json.toJSONString(),"application/json");
    }

}
