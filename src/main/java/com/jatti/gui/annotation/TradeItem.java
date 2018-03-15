package com.jatti.gui.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(value = TradeItems.class)
public @interface TradeItem {

    String tradeCost1();

    String tradeCost2() default "";

    String tradeResult();

    int maxUses() default 1;

}
