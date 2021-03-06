package com.ericbarch.bitpulselib.exchanges.btcchina;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.Currencies;
import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;

public class BtcChina implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public BtcChina(final int currency, final ExchangeManager mgr) {
		// ticker request
		requests.add(new ExchangeRequest("https://data.btcchina.com/data/ticker", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double bid = Double.parseDouble(json.getJSONObject("ticker").getString("buy"));
					final double ask = Double.parseDouble(json.getJSONObject("ticker").getString("sell"));
					final double last = Double.parseDouble(json.getJSONObject("ticker").getString("last"));
					final double vol = Double.parseDouble(json.getJSONObject("ticker").getString("vol"));
					
					// feed results to manager
					mgr.onBid(bid, Currencies.CNY);
					mgr.onAsk(ask, Currencies.CNY);
					mgr.onPrice(last, Currencies.CNY);
					mgr.onVolume(vol);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(int statusCode) {
				mgr.onConnectionError();
			}
		}));
	}
	
	@Override
	public ArrayList<ExchangeRequest> getRequests() {
		return requests;
	}
	
	@Override
	public void onDestroy() {

	}
}