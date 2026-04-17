# Use a lightweight Python base image
FROM python:3.10-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the requirements file and install dependencies
# We do this before copying the script to leverage Docker's layer caching
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy your GlassBox ML script
COPY model.py .

# Command to execute when the container spins up
CMD ["python", "model.py"]