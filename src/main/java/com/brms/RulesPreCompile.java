package com.brms;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RulesPreCompile {


    public static void main(String args[]) {
        List<Serializable> compiledRules = new ArrayList<>();


        String ruleExpression = "input.totalAmount <= 30 && input.currency == \"981\" && input.merchantId == \"testMerchant\"";

        String ruleExpression2 = "input.totalAmount <= 30 && input.currency == \"981\"";

        String ruleExpression3 = "input.totalAmount <= 30";


        compiledRules.add(compile(ruleExpression));

        compiledRules.add(compile(ruleExpression2));
        compiledRules.add(compile(ruleExpression3));




        Transaction tran = new Transaction();
        tran.setTotalAmount(30);
        tran.setCurrency("981");
        tran.setMerchantId("testMerchant");

        Map<String, Object>  tranInput = new HashMap<>();
        tranInput.put("input", tran);

        List<Serializable> compiledRulesMatched =  compiledRules.stream()
                .filter(
                        rule -> {
                            return MVEL.executeExpression(rule, tranInput).equals(Boolean.TRUE);
                        }
                )
                .collect(Collectors.toList());




        System.out.println(compiledRulesMatched);


        TransactionOut tranOut = new TransactionOut();

        Map<String, Object> actionInput = new HashMap<>();
        actionInput.put("output", tranOut);
        MVEL.evalToBoolean("output.setOutput(\"com.brms.TransactionOut\");", actionInput);
        System.out.println(tranOut.getOutput());


    }

    public static Serializable compile(String expression)  {

        ParserContext context = new ParserContext();
        context.setStrictTypeEnforcement(true);
        context.addInput("input", Transaction.class);

        return MVEL.compileExpression(expression, context);

    }

}
