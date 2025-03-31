// Constants
const CHART_DIMENSIONS = {
    width: window.innerWidth * 0.8,
    height: 300,
    margin: { top: 20, right: 20, bottom: 30, left: 50 }
};

// Chart state
const chartState = {
    sessionId: null,
    websocket: null,
    data: []
};

// Chart elements
const chartElements = {
    paths: {},
    lines: {},
    axes: {},
    scales: {},
    svgs: {}
};

/**
 * Initializes D3 charts
 */
function initD3() {
    // Initialize scales
    chartElements.scales.x = d3.scaleTime()
        .range([CHART_DIMENSIONS.margin.left, CHART_DIMENSIONS.width - CHART_DIMENSIONS.margin.right]);

    chartElements.scales.y = {
        speed: d3.scaleLinear().range([CHART_DIMENSIONS.height - CHART_DIMENSIONS.margin.bottom, CHART_DIMENSIONS.margin.top]),
        elevation: d3.scaleLinear().range([CHART_DIMENSIONS.height - CHART_DIMENSIONS.margin.bottom, CHART_DIMENSIONS.margin.top]),
        distance: d3.scaleLinear().range([CHART_DIMENSIONS.height - CHART_DIMENSIONS.margin.bottom, CHART_DIMENSIONS.margin.top])
    };

    // Create SVG containers
    createChartContainers();

    // Initialize axes and lines
    initializeAxesAndLines();
}

/**
 * Creates SVG containers for each chart
 */
function createChartContainers() {
    const chartTypes = ['speed', 'elevation', 'distance'];
    const chartTitles = {
        speed: 'Speed Over Time',
        elevation: 'Elevation Gain Over Time',
        distance: 'Distance Over Time'
    };

    chartTypes.forEach(type => {
        chartElements.svgs[type] = createSVGContainer(type, chartTitles[type]);
    });
}

/**
 * Creates a single SVG container
 */
function createSVGContainer(type, title) {
    const svg = d3.select(`#${type}-chart`)
        .append('svg')
        .attr('width', CHART_DIMENSIONS.width)
        .attr('height', CHART_DIMENSIONS.height);

    svg.append('text')
        .attr('x', CHART_DIMENSIONS.width / 2)
        .attr('y', CHART_DIMENSIONS.margin.top)
        .attr('text-anchor', 'middle')
        .text(title);

    return svg;
}

/**
 * Updates scales based on current data
 */
function updateScales() {
    // Update x scale domain based on time range
    chartElements.scales.x.domain(d3.extent(chartState.data, d => d.timestamp));

    // Update y scales for each metric
    Object.keys(chartElements.scales.y).forEach(metric => {
        chartElements.scales.y[metric].domain([
            0,
            d3.max(chartState.data, d => d[metric]) * 1.1 // Add 10% padding
        ]);
    });
}

/**
 * Updates axes for all charts
 */
function updateAxes() {
    const xAxis = d3.axisBottom(chartElements.scales.x);

    Object.entries(chartElements.axes).forEach(([metric, axes]) => {
        // Update x-axis
        axes.x.call(xAxis);

        // Update y-axis
        const yAxis = d3.axisLeft(chartElements.scales.y[metric]);
        axes.y.call(yAxis);
    });
}

/**
 * Updates path elements with new data
 */
function updatePaths() {
    Object.entries(chartElements.paths).forEach(([metric, path]) => {
        const line = d3.line()
            .x(d => chartElements.scales.x(d.timestamp))
            .y(d => chartElements.scales.y[metric](d[metric]));

        path.datum(chartState.data)
            .attr('d', line);
    });
}

/**
 * Initializes axes and lines for all charts
 */
function initializeAxesAndLines() {
    const chartTypes = ['speed', 'elevation', 'distance'];
    const colors = {
        speed: 'steelblue',
        elevation: 'green',
        distance: 'red'
    };

    chartTypes.forEach(type => {
        // Create axes
        chartElements.axes[type] = {
            x: chartElements.svgs[type].append('g')
                .attr('transform', `translate(0,${CHART_DIMENSIONS.height - CHART_DIMENSIONS.margin.bottom})`)
                .call(d3.axisBottom(chartElements.scales.x)),
            y: chartElements.svgs[type].append('g')
                .attr('transform', `translate(${CHART_DIMENSIONS.margin.left},0)`)
                .call(d3.axisLeft(chartElements.scales.y[type]))
        };

        // Create path elements
        chartElements.paths[type] = chartElements.svgs[type].append('path')
            .attr('fill', 'none')
            .attr('stroke', colors[type])
            .attr('stroke-width', 1.5);
    });
}

/**
 * Updates all charts with new data
 */
function updateCharts() {
    if (chartState.data.length === 0) return;

    updateScales();
    updateAxes();
    updatePaths();
}

/**
 * Handles websocket message events
 */
function handleWebSocketMessage(event) {
    if (event.data === "RIDE_NOT_YET_STARTED") {
        setLivenessState("Ride has not yet started");
        return;
    }

    if (event.data === "RIDE_FINISHED") {
        setLivenessState("Ride is finished!");
        return;
    }

    const payload = JSON.parse(event.data);
    const newData = payload.slice(chartState.data.length);

    newData.forEach(dataPoint => {
        addRow(JSON.stringify(dataPoint));
        addDataPoint(dataPoint);
    });

    setLivenessState("Live!");
}

function setLivenessState(title) {
    document.getElementById("title").textContent = title
}

/**
 * Initializes websocket connection
 */
function startWebsocket(websocketUrl) {
    chartState.websocket = new WebSocket(websocketUrl + chartState.sessionId);
    chartState.websocket.onopen = () => console.log("Socket connection opened");
    chartState.websocket.onmessage = handleWebSocketMessage;
    chartState.websocket.onclose = () => console.log("Socket connection closed");
}

// Event Listeners
window.onbeforeunload = () => {
    if (chartState.websocket) {
        chartState.websocket.onclose = null;
        chartState.websocket.close();
    }
};

// Initialize site
function siteLoaded(localSessionId, websocketUrl) {
    console.log("Initializing for sessionId " + localSessionId);
    chartState.sessionId = localSessionId;
    initD3();
    startWebsocket(websocketUrl);
}

/**
 * Adds a new row to the data table
 * @param {string} update - Stringified data point to display
 */
function addRow(update) {
    const table = document.getElementById("table").getElementsByTagName('tbody')[0];
    const newRow = table.insertRow(0); // Insert at top
    const cell = newRow.insertCell(0);
    cell.textContent = update;

    // Optional: Limit number of rows to prevent excessive DOM elements
    const maxRows = 50;
    while (table.rows.length > maxRows) {
        table.deleteRow(table.rows.length - 1);
    }
}

/**
 * Adds a new data point to the chart state and updates visualization
 * @param {Object} dataPoint - New data point from websocket
 */
function addDataPoint(dataPoint) {
    // Transform incoming data to match chart format
    const formattedData = {
        timestamp: new Date(), // Current timestamp
        speed: dataPoint.averageSpeed || 0,
        elevation: dataPoint.totalElevation || 0,
        distance: dataPoint.totalDistance || 0
    };

    // Add to chart state
    chartState.data.push(formattedData);

    // Optional: Limit data points to prevent memory issues
    const maxDataPoints = 1000;
    if (chartState.data.length > maxDataPoints) {
        chartState.data = chartState.data.slice(-maxDataPoints);
    }

    // Update charts with new data
    updateCharts();
}