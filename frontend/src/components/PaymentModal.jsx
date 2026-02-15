// import React, { useState, useEffect } from 'react';
// import EventService from '../services/EventService';
// import '../styles/PaymentModal.css';

// const PaymentModal = ({ event, onClose, onSuccess }) => {
//   const [loading, setLoading] = useState(false);
//   const [orderId, setOrderId] = useState(null);
//   const [user, setUser] = useState(null);

//   useEffect(() => {
//     const userData = localStorage.getItem('user');
//     if (userData) {
//       setUser(JSON.parse(userData));
//     }
//     initializePayment();
//   }, []);

//   const initializePayment = async () => {
//     try {
//       setLoading(true);
//       const userData = JSON.parse(localStorage.getItem('user'));
//       const email = userData?.email || 'user@example.com';
      
//       const response = await EventService.createPaymentOrder(event.id, email);
//       if (response.success && response.data) {
//         // Parse the order data (it comes as a JSON string)
//         let orderData;
//         if (typeof response.data === 'string') {
//           orderData = JSON.parse(response.data);
//         } else {
//           orderData = response.data;
//         }
        
//         setOrderId(orderData.id);
//         loadRazorpayScript(orderData);
//       } else {
//         alert(response.message || 'Failed to initialize payment');
//         onClose();
//       }
//     } catch (error) {
//       console.error('Payment initialization error:', error);
//       alert('Error initializing payment');
//       onClose();
//     } finally {
//       setLoading(false);
//     }
//   };

//   const loadRazorpayScript = (orderData) => {
//     // Check if script already exists
//     if (window.Razorpay) {
//       initializeRazorpay(orderData);
//       return;
//     }

//     const script = document.createElement('script');
//     script.src = 'https://checkout.razorpay.com/v1/checkout.js';
//     script.onload = () => {
//       initializeRazorpay(orderData);
//     };
//     script.onerror = () => {
//       alert('Failed to load Razorpay SDK');
//       onClose();
//     };
//     document.body.appendChild(script);
//   };

//   const initializeRazorpay = (orderData) => {
//     // Note: Razorpay key ID should be set in environment or come from backend
//     const razorpayKeyId = process.env.REACT_APP_RAZORPAY_KEY_ID || 'rzp_test_key';
    
//     const options = {
//       key: razorpayKeyId,
//       amount: orderData.amount,
//       currency: orderData.currency || 'INR',
//       name: 'Club Events Dashboard',
//       description: `Payment for ${event.name}`,
//       order_id: orderData.id,
//       handler: async (response) => {
//         await verifyPayment(response);
//       },
//       prefill: {
//         name: user?.name || '',
//         email: user?.email || '',
//       },
//       theme: {
//         color: '#000000'
//       }
//     };

//     const razorpay = new window.Razorpay(options);
//     razorpay.on('payment.failed', (response) => {
//       alert('Payment failed. Please try again.');
//       onClose();
//     });
//     razorpay.open();
//   };

//   const verifyPayment = async (paymentResponse) => {
//     try {
//       setLoading(true);
//       const verifyResponse = await EventService.verifyPayment({
//         razorpay_order_id: paymentResponse.razorpay_order_id,
//         razorpay_payment_id: paymentResponse.razorpay_payment_id,
//         razorpay_signature: paymentResponse.razorpay_signature
//       });

//       if (verifyResponse.success) {
//         // After payment verification, register for the event
//         const userData = JSON.parse(localStorage.getItem('user'));
//         const userId = userData?.id;
        
//         if (userId) {
//           const registerResponse = await EventService.registerForEvent(event.id, userId);
//           if (registerResponse.success) {
//             alert('Payment successful! You are now registered for the event.');
//             onSuccess();
//           } else {
//             alert('Payment successful but registration failed. Please contact support.');
//           }
//         } else {
//           alert('Payment successful but user information not found.');
//         }
//       } else {
//         alert(verifyResponse.message || 'Payment verification failed');
//       }
//     } catch (error) {
//       console.error('Payment verification error:', error);
//       alert('Error verifying payment');
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="payment-modal-overlay" onClick={onClose}>
//       <div className="payment-modal" onClick={(e) => e.stopPropagation()}>
//         <button className="payment-close-button" onClick={onClose}>×</button>
//         <div className="payment-content">
//           <h2>Processing Payment</h2>
//           <p>Event: {event.name}</p>
//           <p>Amount: ₹{event.entryFee}</p>
//           {loading && <div className="payment-loading">Loading payment gateway...</div>}
//           <p className="payment-note">You will be redirected to Razorpay payment gateway...</p>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default PaymentModal;


import React, { useEffect, useState } from 'react';
import EventService from '../services/EventService';
import '../styles/PaymentModal.css';

const PaymentModal = ({ event, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (!storedUser || !event) {
      alert('User or event data missing');
      onClose();
      return;
    }

    const parsedUser = JSON.parse(storedUser);
    setUser(parsedUser);
    initializePayment(parsedUser);

    // eslint-disable-next-line
  }, []);

  const initializePayment = async (currentUser) => {
    try {
      setLoading(true);

      const response = await EventService.createPaymentOrder(
        event.id,
        currentUser.email
      );

      if (!response?.success || !response.data) {
        throw new Error(response?.message || 'Payment order creation failed');
      }
      console.log("response data  for payment order", response);
      
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
  };

  const loadRazorpayScript = (orderData, currentUser) => {
    if (window.Razorpay) {
      openRazorpay(orderData, currentUser);
      return;
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';

    script.onload = () => openRazorpay(orderData, currentUser);
    script.onerror = () => {
      alert('Failed to load payment gateway');
      onClose();
    };

    document.body.appendChild(script);
  };

  const openRazorpay = (orderData, currentUser) => {
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
  };

  const verifyPayment = async (paymentResponse) => {
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
  };

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
