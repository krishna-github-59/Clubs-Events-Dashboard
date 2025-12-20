import React, { useState, useEffect } from 'react';
import EventService from '../services/EventService';
import '../styles/PaymentModal.css';

const PaymentModal = ({ event, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [orderId, setOrderId] = useState(null);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const userData = localStorage.getItem('user');
    if (userData) {
      setUser(JSON.parse(userData));
    }
    initializePayment();
  }, []);

  const initializePayment = async () => {
    try {
      setLoading(true);
      const userData = JSON.parse(localStorage.getItem('user'));
      const email = userData?.email || 'user@example.com';
      
      const response = await EventService.createPaymentOrder(event.id, email);
      if (response.success && response.data) {
        // Parse the order data (it comes as a JSON string)
        let orderData;
        if (typeof response.data === 'string') {
          orderData = JSON.parse(response.data);
        } else {
          orderData = response.data;
        }
        
        setOrderId(orderData.id);
        loadRazorpayScript(orderData);
      } else {
        alert(response.message || 'Failed to initialize payment');
        onClose();
      }
    } catch (error) {
      console.error('Payment initialization error:', error);
      alert('Error initializing payment');
      onClose();
    } finally {
      setLoading(false);
    }
  };

  const loadRazorpayScript = (orderData) => {
    // Check if script already exists
    if (window.Razorpay) {
      initializeRazorpay(orderData);
      return;
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => {
      initializeRazorpay(orderData);
    };
    script.onerror = () => {
      alert('Failed to load Razorpay SDK');
      onClose();
    };
    document.body.appendChild(script);
  };

  const initializeRazorpay = (orderData) => {
    // Note: Razorpay key ID should be set in environment or come from backend
    const razorpayKeyId = process.env.REACT_APP_RAZORPAY_KEY_ID || 'rzp_test_key';
    
    const options = {
      key: razorpayKeyId,
      amount: orderData.amount,
      currency: orderData.currency || 'INR',
      name: 'Club Events Dashboard',
      description: `Payment for ${event.name}`,
      order_id: orderData.id,
      handler: async (response) => {
        await verifyPayment(response);
      },
      prefill: {
        name: user?.name || '',
        email: user?.email || '',
      },
      theme: {
        color: '#000000'
      }
    };

    const razorpay = new window.Razorpay(options);
    razorpay.on('payment.failed', (response) => {
      alert('Payment failed. Please try again.');
      onClose();
    });
    razorpay.open();
  };

  const verifyPayment = async (paymentResponse) => {
    try {
      setLoading(true);
      const verifyResponse = await EventService.verifyPayment({
        razorpay_order_id: paymentResponse.razorpay_order_id,
        razorpay_payment_id: paymentResponse.razorpay_payment_id,
        razorpay_signature: paymentResponse.razorpay_signature
      });

      if (verifyResponse.success) {
        // After payment verification, register for the event
        const userData = JSON.parse(localStorage.getItem('user'));
        const userId = userData?.id;
        
        if (userId) {
          const registerResponse = await EventService.registerForEvent(event.id, userId);
          if (registerResponse.success) {
            alert('Payment successful! You are now registered for the event.');
            onSuccess();
          } else {
            alert('Payment successful but registration failed. Please contact support.');
          }
        } else {
          alert('Payment successful but user information not found.');
        }
      } else {
        alert(verifyResponse.message || 'Payment verification failed');
      }
    } catch (error) {
      console.error('Payment verification error:', error);
      alert('Error verifying payment');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="payment-modal-overlay" onClick={onClose}>
      <div className="payment-modal" onClick={(e) => e.stopPropagation()}>
        <button className="payment-close-button" onClick={onClose}>×</button>
        <div className="payment-content">
          <h2>Processing Payment</h2>
          <p>Event: {event.name}</p>
          <p>Amount: ₹{event.entryFee}</p>
          {loading && <div className="payment-loading">Loading payment gateway...</div>}
          <p className="payment-note">You will be redirected to Razorpay payment gateway...</p>
        </div>
      </div>
    </div>
  );
};

export default PaymentModal;
