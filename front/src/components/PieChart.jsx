import React from 'react';
import '../styles/PieChart.css'; // Стили для диаграммы

const COLORS = [
    '#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#A239EA',
    '#FF6666', '#33CCCC', '#FFCC66', '#66FF99', '#CC99FF',
    '#FF9933', '#3399FF', '#99FF66', '#FF66CC', '#DAA520'
];

const PieChart = ({ data, title }) => {
    if (!data || data.length === 0) {
        return <p>Нет данных для отображения {title.toLowerCase()}.</p>;
    }

    const total = data.reduce((sum, item) => sum + item.value, 0);
    if (total === 0) {
        return <p>Нет данных для отображения {title.toLowerCase()} (все значения нулевые).</p>;
    }

    let accumulatedAngle = 0;

    const getCoordinatesForPercent = (percent) => {
        const x = Math.cos(2 * Math.PI * percent);
        const y = Math.sin(2 * Math.PI * percent);
        return [x, y];
    };

    return (
        <div className="pie-chart-container">
            <h3>{title}</h3>
            <svg viewBox="-1 -1 2 2" style={{ transform: 'rotate(-90deg)' }} className="pie-chart-svg">
                {data.map((item, index) => {
                    const percent = item.value / total;
                    // Пропускаем отрисовку сегмента, если его значение 0
                    if (percent === 0) return null;

                    const [startX, startY] = getCoordinatesForPercent(accumulatedAngle / 360);
                    accumulatedAngle += percent * 360;
                    const [endX, endY] = getCoordinatesForPercent(accumulatedAngle / 360);
                    const largeArcFlag = percent > 0.5 ? 1 : 0;

                    const pathData = [
                        `M ${startX} ${startY}`, // Move
                        `A 1 1 0 ${largeArcFlag} 1 ${endX} ${endY}`, // Arc
                        `L 0 0`, // Line to center
                    ].join(' ');

                    return (
                        <path
                            key={item.name}
                            d={pathData}
                            fill={COLORS[index % COLORS.length]}
                        />
                    );
                })}
            </svg>
            <div className="pie-chart-legend">
                {data.map((item, index) => (
                    <div key={item.name} className="legend-item">
            <span
                className="legend-color-box"
                style={{ backgroundColor: COLORS[index % COLORS.length] }}
            ></span>
                        {item.name}: {item.value} ({(item.value / total * 100).toFixed(1)}%)
                    </div>
                ))}
            </div>
        </div>
    );
};

export default PieChart;
