import React, { useEffect, useRef, useState, useCallback } from 'react';
import EventService from '../services/EventService';
import '../styles/PaymentModal.css';

const PaymentModal = ({ event, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const hasInitialized = useRef(null);

  const verifyPayment = useCallback(async (paymentResponse) => {
    try {
      setLoading(true);

      const verifyRes = await EventService.verifyPayment({
        razorpay_order_id: paymentResponse.razorpay_order_id,
        razorpay_payment_id: paymentResponse.razorpay_payment_id,
        razorpay_signature: paymentResponse.razorpay_signature,
      });

      if (!verifyRes.success) {
        throw new Error(verifyRes.message || 'Verification failed');
      }

      // Re-attempt registration (backend now allows it)
      await EventService.registerForEvent({
        eventId: event.id
      });

      alert('Payment successful! You are registered.');
      onSuccess();
    } catch (err) {
      console.error(err);
      alert(
        'Payment successful, but registration failed. Please contact support.'
      );
      onSuccess(); // still allow user forward
    } finally {
      setLoading(false);
    }
  },[event.id, onSuccess]);

  const openRazorpay = useCallback((orderData, currentUser) => {
    const options = {
      key: orderData.keyId,
      amount: orderData.amount,
      currency: orderData.currency || 'INR',
      description: `Payment for ${event.name}`,
      order_id: orderData.id,

      handler: verifyPayment,

      prefill: {
        name: currentUser.name,
        email: currentUser.email,
      },

      modal: {
        ondismiss: () => {
          alert("Payment cancelled.");
          onClose();
        },
      },

      theme: {
        color: '#000000',
      },
    };

    const razorpay = new window.Razorpay(options);

    razorpay.on('payment.failed', () => {
      alert('Payment failed. Please try again.');
      onClose();
    });

    razorpay.open();
  },[event.name, verifyPayment, onClose]);

  const loadRazorpayScript = useCallback((orderData, currentUser) => {
    if (window.Razorpay) {
      openRazorpay(orderData, currentUser);
      return;
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;

    script.onload = () => openRazorpay(orderData, currentUser);
    script.onerror = () => {
      alert('Failed to load payment gateway');
      onClose();
    };

    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  },[onClose, openRazorpay]);

    const initializePayment = useCallback(async (currentUser) => {
    try {
      setLoading(true);

      const response = await EventService.createPaymentOrder(
        event.id,
        currentUser.email
      );

      if (!response?.success || !response.data) {
        throw new Error(response?.message || 'Payment order creation failed');
      }
      
      const orderData =
        typeof response.data === 'string'
          ? JSON.parse(response.data)
          : response.data;
      console.log("Order data", orderData);

      loadRazorpayScript(orderData, currentUser);
    } catch (err) {
      console.error(err);
      alert('Unable to initialize payment');
      onClose();
    } finally {
      setLoading(false);
    }
  },[event.id, loadRazorpayScript, onClose]);

  useEffect(() => {
    if (hasInitialized.current) return;
    hasInitialized.current = true;

    const storedUser = localStorage.getItem("user");
    if (!storedUser || !event) {
      alert("User or event data missing");
      onClose();
      return;
    }

    const parsedUser = JSON.parse(storedUser);
    initializePayment(parsedUser);
  }, [event, onClose, initializePayment]);


  return (
    <div className="payment-modal-overlay" onClick={onClose}>
      <div className="payment-modal" onClick={(e) => e.stopPropagation()}>
        <button className="payment-close-button" onClick={onClose}>
          ×
        </button>

        <div className="payment-content">
          <h2>Processing Payment</h2>
          <p><strong>Event:</strong> {event.name}</p>
          <p><strong>Amount:</strong> ₹{event.entryFee}</p>

          {loading && (
            <div className="payment-loading">
              Initializing payment gateway...
            </div>
          )}

          <p className="payment-note">
            You will be redirected to Razorpay to complete the payment.
          </p>
        </div>
      </div>
    </div>
  );
};

export default PaymentModal;
