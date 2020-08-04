package co.thenets.brisk.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import co.thenets.brisk.enums.AcceptedPaymentType;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.enums.PaymentMethodType;
import co.thenets.brisk.enums.PushType;

/**
 * Created by DAVID on 02/04/2014.
 */
public class GsonTypeAdapters
{
    public static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>()
    {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException
        {
            if (value == null)
            {
                out.nullValue();
            }
            else
            {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException
        {
            JsonToken peek = in.peek();
            switch (peek)
            {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

    public static final TypeAdapter<PushType> numberAsPushTypeAdapter = new TypeAdapter<PushType>()
    {
        @Override
        public void write(JsonWriter out, PushType value) throws IOException
        {
            if (value == null)
            {
                out.nullValue();
            }
            else
            {
                out.value(value.toString());
            }
        }

        @Override
        public PushType read(JsonReader in) throws IOException
        {
            JsonToken peek = in.peek();
            switch (peek)
            {
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return PushType.fromInt(in.nextInt());
                case STRING:
                    return PushType.fromInt(Integer.parseInt(in.nextString()));
                default:
                    throw new IllegalStateException("Expected String or NUMBER but was " + peek);
            }
        }
    };

    public static final TypeAdapter<OrderState> numberAsOrderStateAdapter = new TypeAdapter<OrderState>()
    {
        @Override
        public void write(JsonWriter out, OrderState value) throws IOException
        {
            if (value == null)
            {
                out.nullValue();
            }
            else
            {
                out.value(value.toString());
            }
        }

        @Override
        public OrderState read(JsonReader in) throws IOException
        {
            JsonToken peek = in.peek();
            switch (peek)
            {
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return OrderState.fromInt(in.nextInt());
                case STRING:
                    return OrderState.fromInt(Integer.parseInt(in.nextString()));
                default:
                    throw new IllegalStateException("Expected String or NUMBER but was " + peek);
            }
        }
    };

    public static final TypeAdapter<PaymentMethodType> numberAsPaymentMethodTypeAdapter = new TypeAdapter<PaymentMethodType>()
    {
        @Override
        public void write(JsonWriter out, PaymentMethodType paymentMethodType) throws IOException
        {
            if (paymentMethodType == null)
            {
                out.nullValue();
            }
            else
            {
                out.value(paymentMethodType.getValue());
            }
        }

        @Override
        public PaymentMethodType read(JsonReader in) throws IOException
        {
            JsonToken peek = in.peek();
            switch (peek)
            {
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return PaymentMethodType.fromInt(in.nextInt());
                case STRING:
                    return PaymentMethodType.fromInt(Integer.parseInt(in.nextString()));
                default:
                    throw new IllegalStateException("Expected String or NUMBER but was " + peek);
            }
        }
    };

    public static final TypeAdapter<AcceptedPaymentType> numberAscceptedPaymentTypeAdapter = new TypeAdapter<AcceptedPaymentType>()
    {
        @Override
        public void write(JsonWriter out, AcceptedPaymentType acceptedPaymentType) throws IOException
        {
            if (acceptedPaymentType == null)
            {
                out.nullValue();
            }
            else
            {
                out.value(acceptedPaymentType.getValue());
            }
        }

        @Override
        public AcceptedPaymentType read(JsonReader in) throws IOException
        {
            JsonToken peek = in.peek();
            switch (peek)
            {
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return AcceptedPaymentType.fromInt(in.nextInt());
                case STRING:
                    return AcceptedPaymentType.fromInt(Integer.parseInt(in.nextString()));
                default:
                    throw new IllegalStateException("Expected String or NUMBER but was " + peek);
            }
        }
    };
}
