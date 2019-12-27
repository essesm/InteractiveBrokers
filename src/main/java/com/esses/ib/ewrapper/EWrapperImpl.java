package com.esses.ib.ewrapper;

import com.esses.InteractiveBrokersAPI;
import com.esses.ib.TickType;
import com.ib.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * @see <a href="https://interactivebrokers.github.io/tws-api/interfaceIBApi_1_1EWrapper.html">EWrapper Interface Reference</a>
 */
@Slf4j
public class EWrapperImpl implements EWrapper {

    private final EJavaSignal readerSignal;
    private final EClientSocket clientSocket;
    private final InteractiveBrokersAPI api;

    /**
     * @see <a href="https://interactivebrokers.github.io/tws-api/message_codes.html">Message Codes</a>
     */
    private static final int MARKET_DATA_FARM_CONNECTION_IS_OK = 2104;
    private static final int HISTORICAL_DATA_FARM_IS_CONNECTED = 2106;
    private static final int MARKET_DATA_CONNECTION_INACTIVE = 2108;
    private static final Set<Integer> ERROR_CODES_THAT_ARE_NOT_ERRORS = Set.of(
            MARKET_DATA_FARM_CONNECTION_IS_OK,
            HISTORICAL_DATA_FARM_IS_CONNECTED,
            MARKET_DATA_CONNECTION_INACTIVE
    );

    private static final int COULD_NOT_CONNECT_TO_TWS_ERROR_CODE = 502;
    private static final int NOT_CONNECTED_TO_TWS_ERROR_CODE = 504;
    private static final int BAD_MESSAGE_LENGTH_ERROR_CODE = 507;
    private static final Set<Integer> ERROR_CODES_THAT_SHOULD_THROW_RUNTIME_EXCEPTIONS = Set.of(
            COULD_NOT_CONNECT_TO_TWS_ERROR_CODE,
            NOT_CONNECTED_TO_TWS_ERROR_CODE,
            BAD_MESSAGE_LENGTH_ERROR_CODE
    );

    private static final int PART_OF_REQUESTED_MARKET_DATA_IS_NOT_SUBSCRIBED_ERROR_CODE = 10090;
    private static final int REQUESTED_MARKET_DATA_IS_NOT_SUBSCRIBED_ERROR_CODE = 10167;
    private static final int NO_MARKET_DATA_DURING_COMPETING_LIVE_SESSION = 10197;
    private static final Set<Integer> ERROR_CODES_TO_IGNORE_AT_LEAST_FOR_NOW = Set.of(
            PART_OF_REQUESTED_MARKET_DATA_IS_NOT_SUBSCRIBED_ERROR_CODE,
            REQUESTED_MARKET_DATA_IS_NOT_SUBSCRIBED_ERROR_CODE,
            NO_MARKET_DATA_DURING_COMPETING_LIVE_SESSION
    );

    /**
     * @see <a href="https://interactivebrokers.github.io/tws-api/delayed_data.html">Delayed Data</a>
     */
    private static final int MARKET_DATA_TYPE_DELAYED_MARKET_DATA = 3;

    public EWrapperImpl() {
        readerSignal = new EJavaSignal();
        clientSocket = new EClientSocket(this, readerSignal);

        clientSocket.eConnect("127.0.0.1", 7496, 0);
        clientSocket.reqMarketDataType(MARKET_DATA_TYPE_DELAYED_MARKET_DATA);

        api = new InteractiveBrokersAPI(clientSocket);

        EReader reader = new EReader(clientSocket, readerSignal);
        reader.start();
        new Thread(() -> {
            while (clientSocket.isConnected()) {
                readerSignal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    log.error("Exception processing message", e);
                }
            }
        }, "message-processing-thread").start();
    }

    public InteractiveBrokersAPI getAPI() {
        return api;
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        EWrapperCallbackHandlers.getInstance().onReceiveOptionContractMarketDataPrice(tickerId, TickType.from(field), price);
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        EWrapperCallbackHandlers.getInstance().onReceiveOptionContractMarketDataSize(tickerId, TickType.from(field), size);
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        EWrapperCallbackHandlers.getInstance().onReceiveOptionComputationDetails(tickerId, TickType.from(field), impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice);
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        throw new NotImplementedException("tickGeneric is not implemented");
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        EWrapperCallbackHandlers.getInstance().onReceiveOptionContractMarketDataString(tickerId, TickType.from(tickType), value);
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {
        throw new NotImplementedException("tickEFP is not implemented");
    }

    @Override
    public void orderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        throw new NotImplementedException("orderStatus is not implemented");
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        throw new NotImplementedException("openOrder is not implemented");
    }

    @Override
    public void openOrderEnd() {
        throw new NotImplementedException("openOrderEnd is not implemented");
    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {
        throw new NotImplementedException("updateAccountValue is not implemented");
    }

    @Override
    public void updatePortfolio(Contract contract, double position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        throw new NotImplementedException("updatePortfolio is not implemented");
    }

    @Override
    public void updateAccountTime(String timeStamp) {
        throw new NotImplementedException("updateAccountTime is not implemented");
    }

    @Override
    public void accountDownloadEnd(String accountName) {
        throw new NotImplementedException("accountDownloadEnd is not implemented");
    }

    @Override
    public void nextValidId(int orderId) {
        log.trace("The next valid id to place an order is {}", orderId);
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        EWrapperCallbackHandlers.getInstance().onReceiveOptionContractDetails(reqId, contractDetails);
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        throw new NotImplementedException("bondContractDetails is not implemented");
    }

    @Override
    public void contractDetailsEnd(int reqId) {
        EWrapperCallbackHandlers.getInstance().onRequestOptionChainEnd(reqId);
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        throw new NotImplementedException("execDetails is not implemented");
    }

    @Override
    public void execDetailsEnd(int reqId) {
        throw new NotImplementedException("execDetailsEnd is not implemented");
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
        throw new NotImplementedException("updateMktDepth is not implemented");
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
        throw new NotImplementedException("updateMktDepthL2 is not implemented");
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        throw new NotImplementedException("updateNewsBulletin is not implemented");
    }

    @Override
    public void managedAccounts(String accountsList) {
        log.trace("Found the following managed account(s): {}", accountsList);
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        throw new NotImplementedException("receiveFA is not implemented");
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        throw new NotImplementedException("historicalData is not implemented");
    }

    @Override
    public void scannerParameters(String xml) {
        throw new NotImplementedException("scannerParameters is not implemented");
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
        throw new NotImplementedException("scannerData is not implemented");
    }

    @Override
    public void scannerDataEnd(int reqId) {
        throw new NotImplementedException("scannerDataEnd is not implemented");
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
        throw new NotImplementedException("realtimeBar is not implemented");
    }

    @Override
    public void currentTime(long time) {
        throw new NotImplementedException("currentTime is not implemented");
    }

    @Override
    public void fundamentalData(int reqId, String data) {
        throw new NotImplementedException("fundamentalData is not implemented");
    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract underComp) {
        throw new NotImplementedException("deltaNeutralValidation is not implemented");
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        EWrapperCallbackHandlers.getInstance().onRequestOptionContractMarketDataEnd(reqId);
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        log.trace("The market data type for request id {} is {}", reqId, MarketDataType.getField(marketDataType));
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
        throw new NotImplementedException("commissionReport is not implemented");
    }

    @Override
    public void position(String account, Contract contract, double pos, double avgCost) {
        throw new NotImplementedException("position is not implemented");
    }

    @Override
    public void positionEnd() {
        throw new NotImplementedException("positionEnd is not implemented");
    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        throw new NotImplementedException("accountSummary is not implemented");
    }

    @Override
    public void accountSummaryEnd(int reqId) {
        throw new NotImplementedException("accountSummaryEnd is not implemented");
    }

    @Override
    public void verifyMessageAPI(String apiData) {
        throw new NotImplementedException("verifyMessageAPI is not implemented");
    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {
        throw new NotImplementedException("verifyCompleted is not implemented");
    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {
        throw new NotImplementedException("verifyAndAuthMessageAPI is not implemented");
    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
        throw new NotImplementedException("verifyAndAuthCompleted is not implemented");
    }

    @Override
    public void displayGroupList(int reqId, String groups) {
        throw new NotImplementedException("displayGroupList is not implemented");
    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {
        throw new NotImplementedException("displayGroupUpdated is not implemented");
    }

    @Override
    public void error(Exception e) {
        throw new RuntimeException(e);
    }

    @Override
    public void error(String str) {
        throw new RuntimeException(str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        if (ERROR_CODES_THAT_ARE_NOT_ERRORS.contains(errorCode)) {
            return;
        }

        if (ERROR_CODES_TO_IGNORE_AT_LEAST_FOR_NOW.contains(errorCode)) {
            log.trace("Request {}: [{}] \"{}\"", id, errorCode, errorMsg);
            return;
        }

        checkState(ERROR_CODES_THAT_SHOULD_THROW_RUNTIME_EXCEPTIONS.contains(errorCode),
                "Found error code %s (%s), which hasn't been categorized by the developer yet.", errorCode, errorMsg);
        throw new RuntimeException(errorMsg);
    }

    @Override
    public void connectionClosed() {
        throw new NotImplementedException("connectionClosed is not implemented");
    }

    @Override
    public void connectAck() {
        checkState(clientSocket.isConnected());
        log.trace("Client connection has been acknowledged");
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, double pos, double avgCost) {
        throw new NotImplementedException("positionMulti is not implemented");
    }

    @Override
    public void positionMultiEnd(int reqId) {
        throw new NotImplementedException("positionMultiEnd is not implemented");
    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency) {
        throw new NotImplementedException("accountUpdateMulti is not implemented");
    }

    @Override
    public void accountUpdateMultiEnd(int reqId) {
        throw new NotImplementedException("accountUpdateMultiEnd is not implemented");
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {
        throw new NotImplementedException("securityDefinitionOptionalParameter is not implemented");
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
        throw new NotImplementedException("securityDefinitionOptionalParameterEnd is not implemented");
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        throw new NotImplementedException("softDollarTiers is not implemented");
    }
}
