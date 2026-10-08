export default function Home() {
  return (
     <div>
     <h1 className="text-2xl font-semibold mb-6">
        Overview
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        <div className="bg-pink-50 border rounded-lg p-5">
          <p className="text-sm text-gray-500">Total Leads</p>
          <h3 className="text-2xl font-semibold mt-2">0</h3>
        </div>

        <div className="bg-pink-50 border rounded-lg p-5">
          <p className="text-sm text-gray-500">Open Deals</p>
          <h3 className="text-2xl font-semibold mt-2">0</h3>
        </div>

        <div className="bg-pink-50 border rounded-lg p-5">
          <p className="text-sm text-gray-500">Pipeline Value</p>
          <h3 className="text-2xl font-semibold mt-2">₹0</h3>
        </div>
      </div>
    </div>
  );
}