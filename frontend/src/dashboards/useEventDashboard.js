import { useState, useEffect, useCallback } from "react";
import EventService from "../services/EventService";
import { getCategoryName } from "../utils/categoryUtils";


const useEventDashboard = (mode) => {
  const [upcomingEvents, setUpcomingEvents] = useState([]);
  const [filteredUpcomingEvents, setFilteredUpcomingEvents] = useState([]);
  const [pastEvents, setPastEvents] = useState([]);
  const [filteredPastEvents, setFilteredPastEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("All Categories");
  const [totalEventsCount, setTotalEventsCount] = useState(0);

  const categories = [
    "All Categories",
    "Technical",
    "Arts and Culture",
    "Photography",
    "Sports",
  ];

  useEffect(() => {
    setTotalEventsCount(upcomingEvents.length + pastEvents.length);
  }, [upcomingEvents, pastEvents]);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);

      let upcomingRes, pastRes;

      if (mode === "CLUB_ADMIN") {
        upcomingRes = await EventService.getMyClubUpcomingEvents();
        pastRes = await EventService.getMyClubPastEvents();
      } else {
        upcomingRes = await EventService.getAllUpcomingEvents();
        pastRes = await EventService.getAllPastEvents();
      }

      if (upcomingRes.success) {
        setUpcomingEvents(upcomingRes.data || []);
        setFilteredUpcomingEvents(upcomingRes.data || []);
      }

      if (pastRes.success) {
        setPastEvents(pastRes.data || []);
        setFilteredPastEvents(pastRes.data || []);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  },[mode]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const filterEvents = useCallback(() => {
    let filteredUpcoming = [...upcomingEvents];
    let filteredPast = [...pastEvents];

    if (mode === "STUDENT" && selectedCategory !== "All Categories") {
      filteredUpcoming = filteredUpcoming.filter(
        (e) => getCategoryName(e.clubName) === selectedCategory
      );
      filteredPast = filteredPast.filter(
        (e) => getCategoryName(e.clubName) === selectedCategory
      );
    }

    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      filteredUpcoming = filteredUpcoming.filter((e) =>
        e.name.toLowerCase().includes(q)
      );
      filteredPast = filteredPast.filter((e) =>
        e.name.toLowerCase().includes(q)
      );
    }

    setFilteredUpcomingEvents(filteredUpcoming);
    setFilteredPastEvents(filteredPast);
  },[mode, selectedCategory, searchQuery, upcomingEvents, pastEvents]);

  useEffect(() => {
    filterEvents();
  }, [filterEvents]);

  return {
    loading,
    categories,
    searchQuery,
    setSearchQuery,
    selectedCategory,
    setSelectedCategory,
    filteredUpcomingEvents,
    filteredPastEvents,
    totalEventsCount,
    loadData,
  };
};

export default useEventDashboard;
