package ru.ringsplus.app.model;

import java.util.ArrayList;
import java.util.List;

public class StockCollection {

    private static StockCollection sDayCollection;
    private List<DayItem> mDayItemList;
    private List<RingItem> mRingItems;

    private StockCollection() {
        mDayItemList = new ArrayList<>();
        mRingItems = new ArrayList<>();
    };

    static {
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 20*20"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 20*30"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 30*20"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 30*30"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 20*20"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 20*30"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 30*20"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 30*30"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 20*20"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 20*30"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 30*20"));
        StockCollection.getInstance().getRingItems().add(new RingItem( "Кольцо 30*30"));


        //----------------------------------------------------------------

        DayItem dayItem1 = new DayItem(2020, 6, 20);

        OrderItem orderItem1 = new OrderItem("Иванов", "Приезжать после 9 утра", "Петров Игорь Витальевич");
        orderItem1.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(0).getName(), 2));
        orderItem1.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(1).getName(), 1));
        dayItem1.getOrderItemList().add(orderItem1);

        StockCollection.getInstance().getDayCollection().add(dayItem1);

        //------------------------------------------------------------------

        DayItem dayItem2 = new DayItem(2020, 6, 10);

        OrderItem orderItem2 = new OrderItem("ул. Сахарова 12А", "Без установки", "Симонов Виктор Леонидович");
        orderItem2.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(3).getName(), 1));
        dayItem2.getOrderItemList().add(orderItem2);

        OrderItem orderItem3 = new OrderItem("Ирина +79195665034", "Замеры без колец", "Петров Игорь Витальевич");
        dayItem2.getOrderItemList().add(orderItem3);

        OrderItem orderItem4 = new OrderItem("Проспект Шапошникова 12", "тел. 67-45-43", "Симонов Виктор Леонидович");
        orderItem4.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(0).getName(), 3));
        orderItem4.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(1).getName(), 2));
        orderItem4.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(2).getName(), 1));
        dayItem2.getOrderItemList().add(orderItem4);

        StockCollection.getInstance().getDayCollection().add(dayItem2);

        //------------------------------------------------------------------

        DayItem dayItem3 = new DayItem(2020, 7, 15);
        dayItem3.setDayStatus(DayStatus.CloseDay);

        OrderItem orderItem5 = new OrderItem("ул. Сахарова 12А", "Без установки", "Симонов Виктор Леонидович");
        orderItem5.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(3).getName(), 1));
        dayItem3.getOrderItemList().add(orderItem5);

        OrderItem orderItem6 = new OrderItem("Ирина +79195665034", "Замеры без колец", "");
        dayItem3.getOrderItemList().add(orderItem6);

        OrderItem orderItem7 = new OrderItem( "Проспект Шапошникова 12", "тел. 67-45-43", "Симонов Виктор Леонидович");
        orderItem7.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(0).getName(), 3));
        orderItem7.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(1).getName(), 2));
        orderItem7.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(2).getName(), 1));
        dayItem3.getOrderItemList().add(orderItem7);

        StockCollection.getInstance().getDayCollection().add(dayItem3);

        //------------------------------------------------------------------

        DayItem dayItem4 = new DayItem(2020, 7, 25);

        OrderItem orderItem8 = new OrderItem( "ул. Сахарова 12А", "Без установки", "");
        orderItem8.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(3).getName(), 1));
        dayItem4.getOrderItemList().add(orderItem8);

        OrderItem orderItem9 = new OrderItem( "Ирина +79195665034", "Замеры без колец", "");
        dayItem4.getOrderItemList().add(orderItem9);

        OrderItem orderItem10 = new OrderItem( "Проспект Шапошникова 12", "тел. 67-45-43", "Симонов Виктор Леонидович");
        orderItem10.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(0).getName(), 3));
        orderItem10.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(1).getName(), 2));
        orderItem10.getRingOrderItemList().add(new RingOrderItem(StockCollection.getInstance().getRingItems().get(2).getName(), 1));
        dayItem4.getOrderItemList().add(orderItem10);

        StockCollection.getInstance().getDayCollection().add(dayItem4);
    }

    public static StockCollection getInstance() {
        if (sDayCollection == null) {
            sDayCollection = new StockCollection();
        }

      return sDayCollection;
    }

    public List<DayItem> getDayCollection() {
        return mDayItemList;
    }

    public List<RingItem> getRingItems() {
        return mRingItems;
    }

}
